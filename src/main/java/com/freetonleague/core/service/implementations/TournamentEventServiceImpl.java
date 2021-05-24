package com.freetonleague.core.service.implementations;


import com.freetonleague.core.domain.dto.AccountInfoDto;
import com.freetonleague.core.domain.dto.AccountTransactionInfoDto;
import com.freetonleague.core.domain.dto.EventDto;
import com.freetonleague.core.domain.enums.*;
import com.freetonleague.core.domain.model.*;
import com.freetonleague.core.exception.ExceptionMessages;
import com.freetonleague.core.exception.TeamParticipantManageException;
import com.freetonleague.core.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class TournamentEventServiceImpl implements TournamentEventService {

    private final Set<Long> cachedTournamentId = Collections.synchronizedSet(new HashSet<>());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final EventService eventService;
    private final FinancialClientService financialClientService;
    private final TournamentService tournamentService;
    private final TournamentProposalService tournamentProposalService;

    @Lazy
    @Autowired
    private TournamentSeriesService tournamentSeriesService;

    @Lazy
    @Autowired
    private TournamentMatchService tournamentMatchService;

    @Lazy
    @Autowired
    private TournamentRoundService tournamentRoundService;

    @Value("${freetonleague.tournament.auto-start:false}")
    private boolean tournamentAutoStartEnabled;

    @Override
    public EventDto add(EventDto event) {
        log.info("! handle add EventDto");
        return null;
    }

    //every 10 minutes, timout before start 1 min
    @Scheduled(fixedRate = 10 * 60 * 1000, initialDelay = 60 * 1000)
    void monitor() {
        log.debug("^ Run TournamentEventService monitor");

        final Map<Long, Tournament> idToTournament = this.getIdToTournamentMap();
        final Set<Long> keys = new HashSet<>(idToTournament.keySet());

        if (idToTournament.isEmpty()) {
            log.debug("^ Active tournaments were not found. TournamentEventService monitor waits.");
            return;
        }

        if (!cachedTournamentId.isEmpty() && cachedTournamentId.containsAll(keys)) {
            log.debug("^ Tournament events cache was cleaned");
            cachedTournamentId.clear();
        } else {
            keys.removeAll(cachedTournamentId);
        }

        for (Long selectedKey : keys) {
            Tournament tournament = idToTournament.get(selectedKey);
            this.tryMakeStatusUpdateOperations(tournament);
        }
    }

    /**
     * Process tournament status changing
     */
    @Override
    public void processTournamentStatusChange(Tournament tournament, TournamentStatusType newTournamentMatchStatus) {
        log.debug("^ new status changed for tournament {} with new status {}.", tournament, newTournamentMatchStatus);
        if (newTournamentMatchStatus.isCreated()) {
            financialClientService.createAccountByHolderInfo(tournament.getCoreId(),
                    AccountHolderType.TOURNAMENT, tournament.getName());
        }
    }

    /**
     * Process match status changing
     */
    @Override
    public void processMatchStatusChange(TournamentMatch tournamentMatch, TournamentStatusType newTournamentMatchStatus) {
        log.debug("^ status of match was changed from {} to {}. Process match status change in Tournament Event Service.",
                tournamentMatch.getPrevStatus(), newTournamentMatchStatus);
        // check all match is finished to finish the series
        if (newTournamentMatchStatus.isFinished()
                && tournamentMatchService.isAllMatchesFinishedBySeries(tournamentMatch.getTournamentSeries())
                //TODO switch on auto closing series for all type of Tournament System Template
                && tournamentMatch.getTournamentSeries().getTournamentRound().getTournament().getSystemType().isAutoFinishSeriesEnabled()) {
            this.handleSeriesStatusChange(tournamentMatch.getTournamentSeries(), TournamentStatusType.FINISHED);
        }
    }

    /**
     * Process series status changing
     */
    @Override
    public void processSeriesStatusChange(TournamentSeries tournamentSeries, TournamentStatusType newTournamentSeriesStatus) {
        log.debug("^ status of series was changed from {} to {}. Process series status change in Tournament Event Service.",
                tournamentSeries.getPrevStatus(), newTournamentSeriesStatus);
        // check all series is finished to finish the round
        if (newTournamentSeriesStatus.isFinished()
                && tournamentSeriesService.isAllSeriesFinishedByRound(tournamentSeries.getTournamentRound())) {
            this.handleRoundStatusChange(tournamentSeries.getTournamentRound(), TournamentStatusType.FINISHED);
        }
    }

    /**
     * Process round status changing
     */
    @Override
    public void processRoundStatusChange(TournamentRound tournamentRound, TournamentStatusType newTournamentRoundStatus) {
        log.debug("^ status of round was changed from {} to {}. Process round status change in Tournament Event Service.",
                tournamentRound.getPrevStatus(), newTournamentRoundStatus);
        // check all rounds is finished to finish the tournament or to compose new round
        if (newTournamentRoundStatus.isFinished()) {
            if (tournamentRoundService.isAllRoundsFinishedByTournament(tournamentRound.getTournament())) {
                this.handleTournamentStatusChange(tournamentRound.getTournament(), TournamentStatusType.FINISHED);
            } else {
                tournamentRoundService.composeNextRoundForTournament(tournamentRound.getTournament());
            }
        }
    }

    /**
     * Process series dead head for rivals
     */
    @Override
    public void processSeriesDeadHead(TournamentSeries tournamentSeries) {
        log.error("!> We have a dead head in series {}. Create new match manually", tournamentSeries);
    }

    /**
     * Process tournament team proposal status changing
     */
    @Override
    public List<AccountTransactionInfoDto> processTournamentTeamProposalStateChange(TournamentTeamProposal tournamentTeamProposal,
                                                                                    ParticipationStateType newTournamentTeamState) {
        log.debug("^ state of team proposal was changed from {} to {}. Process team proposal state change in Tournament Event Service.",
                tournamentTeamProposal.getPrevState(), newTournamentTeamState);
        List<AccountTransactionInfoDto> paymentList = null;
        if (tournamentTeamProposal.getTournament().getAccessType().isPaid()) {
            if (this.needToPaidParticipationFee(tournamentTeamProposal)) {
                log.debug("^ state of team proposal required participation fee purchase. Try to call withdraw transaction in Tournament Event Service.");
                paymentList = this.tryMakeParticipationFeePayment(tournamentTeamProposal);
            } else if (this.needToRefundParticipationFee(tournamentTeamProposal)) {
                log.debug("^ state of team proposal required refund of participation fee. Try to call debit transaction in Tournament Event Service.");
                this.bankServiceRefundMockMethod(tournamentTeamProposal);
            }
        }
        return paymentList;
    }

    /**
     * Returns sign: if prev status was non-active and new status is active we need to debit money from team
     */
    private boolean needToPaidParticipationFee(TournamentTeamProposal tournamentTeamProposal) {
        return (isNull(tournamentTeamProposal.getPrevState())
                || ParticipationStateType.disabledProposalStateList
                .contains(tournamentTeamProposal.getPrevState()))
                && ParticipationStateType.activeProposalStateList.contains(tournamentTeamProposal.getState());
    }

    /**
     * if prev status was active and new status is non-active we need to refund money to team
     */
    private boolean needToRefundParticipationFee(TournamentTeamProposal tournamentTeamProposal) {
        return nonNull(tournamentTeamProposal.getPrevState())
                && ParticipationStateType.activeProposalStateList.contains(tournamentTeamProposal.getPrevState())
                && ParticipationStateType.disabledProposalStateList.contains(tournamentTeamProposal.getState());
    }

    //TODO delete method
    private void bankServiceRefundMockMethod(TournamentTeamProposal teamProposal) {
        //make some staff
    }

    /**
     * Try to make participation fee and commission from team to tournament
     */
    private List<AccountTransactionInfoDto> tryMakeParticipationFeePayment(TournamentTeamProposal teamProposal) {
        log.debug("^ try to make participation fee and commission to tournament from team.id {} and teamProposal.id {}",
                teamProposal.getTeam().getId(), teamProposal.getId());
        User teamCapitan = teamProposal.getTeam().getCaptain().getUser();
        Tournament tournament = teamProposal.getTournament();

        double teamParticipationFee = tournamentProposalService.calculateTeamParticipationFee(teamProposal);
        AccountInfoDto teamCapitanAccountDto = financialClientService.getAccountByHolderInfo(teamCapitan.getLeagueId(),
                AccountHolderType.USER);
        if (teamCapitanAccountDto.getAmount() < teamParticipationFee) {
            log.warn("~ forbiddenException for create new proposal for team {} to tournament id {} and status {}. " +
                            "Team capitan {} doesn't have enough fund to pay participation fee for all team members",
                    teamProposal.getTeam().getId(), tournament.getId(), tournament.getStatus(), teamCapitan);
            throw new TeamParticipantManageException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_VERIFICATION_ERROR,
                    String.format("Team capitan '%s' doesn't have enough fund to pay participation fee for all team members. " +
                            "Request rejected.", teamCapitan));
        }

        double tournamentOwnerCommissionPercentage = teamProposal.getTournament()
                .getTournamentSettings().getOrganizerCommission() / 100;
        double commissionAmount = teamParticipationFee * tournamentOwnerCommissionPercentage;
        double tournamentFundAmount = teamParticipationFee - commissionAmount;

        AccountInfoDto tournamentOwnerAccountDto = financialClientService.getAccountByHolderInfo(
                teamProposal.getTournament().getCreatedBy().getLeagueId(), AccountHolderType.USER);
        AccountInfoDto tournamentAccountDto = financialClientService.getAccountByHolderInfo(
                tournament.getCoreId(), AccountHolderType.TOURNAMENT);

        List<AccountTransactionInfoDto> participatePaymentList = new ArrayList<>();
        AccountTransactionInfoDto result = financialClientService.applyPurchaseTransaction(
                this.composeParticipationFeeTransaction(teamCapitanAccountDto, tournamentAccountDto, tournamentFundAmount));
        if (isNull(result)) {
            log.warn("~ forbiddenException for create new proposal for team {} to tournament id {}. " +
                            "Error while transferring fund to pat participation fee. Check requested params.",
                    teamProposal.getTeam().getId(), tournament.getId());
            throw new TeamParticipantManageException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_VERIFICATION_ERROR,
                    "Error while transferring fund to pay participation fee. Check requested params.");
        }
        participatePaymentList.add(result);

        result = financialClientService.applyPurchaseTransaction(
                this.composeParticipationCommissionTransaction(teamCapitanAccountDto, tournamentOwnerAccountDto, commissionAmount));
        if (isNull(result)) {
            log.warn("~ forbiddenException for create new proposal for team {} to tournament id {}. " +
                            "Error while transferring fund to pat participation fee. Check requested params.",
                    teamProposal.getTeam().getId(), tournament.getId());
            throw new TeamParticipantManageException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_VERIFICATION_ERROR,
                    "Error while transferring fund to pay commission participation fee. Check requested params.");
        }
        participatePaymentList.add(result);
        return participatePaymentList;
    }

    private AccountTransactionInfoDto composeParticipationFeeTransaction(AccountInfoDto accountSourceDto,
                                                                         AccountInfoDto accountTargetDto,
                                                                         double tournamentFundAmount) {
        return AccountTransactionInfoDto.builder()
                .amount(tournamentFundAmount)
                .sourceAccount(accountSourceDto)
                .targetAccount(accountTargetDto)
                .transactionType(TransactionType.PAYMENT)
                .transactionTemplateType(TransactionTemplateType.TOURNAMENT_ENTRANCE_FEE)
                .build();

    }

    private AccountTransactionInfoDto composeParticipationCommissionTransaction(AccountInfoDto accountSourceDto,
                                                                                AccountInfoDto accountTargetDto,
                                                                                double commissionAmount) {
        return AccountTransactionInfoDto.builder()
                .amount(commissionAmount)
                .sourceAccount(accountSourceDto)
                .targetAccount(accountTargetDto)
                .transactionType(TransactionType.PAYMENT)
                .transactionTemplateType(TransactionTemplateType.TOURNAMENT_ENTRANCE_COMMISSION)
                .build();
    }

    private Map<Long, Tournament> getIdToTournamentMap() {
        return Collections.unmodifiableMap(
                tournamentService.getAllActiveTournament()
                        .stream()
                        .collect(Collectors.toMap(Tournament::getId, tournament -> tournament)));
    }

    private void tryMakeStatusUpdateOperations(Tournament tournament) {
        log.debug("^ try to define events for tournament: {}", tournament.getId());
        final TournamentStatusType tournamentStatus = tournament.getStatus();

        if (tournamentStatus.isBefore(TournamentStatusType.SIGN_UP)
                && tournament.getSignUpStartDate().isBefore(LocalDateTime.now())) {
            this.handleTournamentStatusChange(tournament, TournamentStatusType.SIGN_UP);
        } else if (tournamentStatus.isBefore(TournamentStatusType.ADJUSTMENT)
                && tournament.getSignUpEndDate().isBefore(LocalDateTime.now())) {
            this.handleTournamentStatusChange(tournament, TournamentStatusType.ADJUSTMENT);
        } else if (tournamentAutoStartEnabled && tournamentStatus.isBefore(TournamentStatusType.STARTED)
                && tournament.getStartPlannedDate().isBefore(LocalDateTime.now())) {
            this.handleTournamentStatusChange(tournament, TournamentStatusType.STARTED);
        }
        log.debug("^ tournament {} with status {} were checked, and added to cache", tournament.getName(), tournament.getId());
        cachedTournamentId.add(tournament.getId());
    }

    private void handleTournamentStatusChange(Tournament tournament, TournamentStatusType newTournamentStatus) {
        log.debug("^ handle changing status of tournament to {} in Tournament Event Service.", newTournamentStatus);
        Map<String, Object> updateFields = Map.of(
                "status", newTournamentStatus
        );

        EventDto event = EventDto.builder()
                .id(UUID.randomUUID().toString())
                .message("Change status of Tournament")
                .eventOperationType(EventOperationType.UPDATE_FIELDS)
                .eventTopic(EventProducerModelType.TOURNAMENT)
                .modelId(tournament.getId().toString())
                .modelData(updateFields)
                .createdDate(LocalDateTime.now())
                .build();
        try {
            log.debug("Not implement to send kafka event in handleTournamentStatusChange: {}", event);
//            eventService.sendEvent(event);
        } catch (Exception exc) {
            log.error("Error in handleStatusChange: {}", exc.getMessage());
        }
        //TODO удалить непосредственный вызов изменения данных и разработать обработчик сообщений из Kafka
        tournament.setStatus(newTournamentStatus);
        tournamentService.editTournament(tournament);
    }

    private void handleRoundStatusChange(TournamentRound tournamentRound, TournamentStatusType newTournamentRoundStatus) {
        log.debug("^ handle changing status of round to {} in Tournament Event Service.", newTournamentRoundStatus);
        Map<String, Object> updateFields = Map.of(
                "status", newTournamentRoundStatus
        );

        EventDto event = EventDto.builder()
                .id(UUID.randomUUID().toString())
                .message("Change status of Tournament Round")
                .eventOperationType(EventOperationType.UPDATE_FIELDS)
                .eventTopic(EventProducerModelType.TOURNAMENT_ROUND)
                .modelId(tournamentRound.getId().toString())
                .modelData(updateFields)
                .createdDate(LocalDateTime.now())
                .build();
        try {
            eventService.sendEvent(event);
        } catch (Exception exc) {
            log.error("Error in handleStatusChange: {}", exc.getMessage());
        }
        //TODO удалить непосредственный вызов изменения данных и разработать обработчик сообщений из Kafka
        tournamentRound.setStatus(newTournamentRoundStatus);
        tournamentRoundService.editRound(tournamentRound);
    }

    private void handleSeriesStatusChange(TournamentSeries tournamentSeries, TournamentStatusType newTournamentSeriesStatus) {
        log.debug("^ handle changing status of series to {} in Tournament Event Service.", newTournamentSeriesStatus);
        Map<String, Object> updateFields = Map.of(
                "status", newTournamentSeriesStatus
        );

        EventDto event = EventDto.builder()
                .id(UUID.randomUUID().toString())
                .message("Change status of Tournament Series")
                .eventOperationType(EventOperationType.UPDATE_FIELDS)
                .eventTopic(EventProducerModelType.TOURNAMENT_MATCH)
                .modelId(tournamentSeries.getId().toString())
                .modelData(updateFields)
                .createdDate(LocalDateTime.now())
                .build();
        try {
            eventService.sendEvent(event);
        } catch (Exception exc) {
            log.error("Error in handleStatusChange: {}", exc.getMessage());
        }
        //TODO удалить непосредственный вызов изменения данных и разработать обработчик сообщений из Kafka
        tournamentSeries.setStatus(newTournamentSeriesStatus);
        tournamentSeriesService.editSeries(tournamentSeries);
    }

    private void handleMatchStatusChange(TournamentMatch tournamentMatch, TournamentStatusType newTournamentMatchStatus) {
        log.debug("^ handle changing status of match to {} in Tournament Event Service.", newTournamentMatchStatus);
        Map<String, Object> updateFields = Map.of(
                "status", newTournamentMatchStatus
        );

        EventDto event = EventDto.builder()
                .id(UUID.randomUUID().toString())
                .message("Change status of Tournament Match")
                .eventOperationType(EventOperationType.UPDATE_FIELDS)
                .eventTopic(EventProducerModelType.TOURNAMENT_MATCH)
                .modelId(tournamentMatch.getId().toString())
                .modelData(updateFields)
                .createdDate(LocalDateTime.now())
                .build();
        try {
            eventService.sendEvent(event);
        } catch (Exception exc) {
            log.error("Error in handleStatusChange: {}", exc.getMessage());
        }
    }
}
