package com.freetonleague.core.service.tournament.implementations;


import com.freetonleague.core.domain.dto.EventDto;
import com.freetonleague.core.domain.dto.finance.AccountInfoDto;
import com.freetonleague.core.domain.dto.finance.AccountTransactionInfoDto;
import com.freetonleague.core.domain.enums.EventOperationType;
import com.freetonleague.core.domain.enums.EventProducerModelType;
import com.freetonleague.core.domain.enums.ParticipationStateType;
import com.freetonleague.core.domain.enums.finance.AccountHolderType;
import com.freetonleague.core.domain.enums.finance.AccountTransactionStatusType;
import com.freetonleague.core.domain.enums.finance.AccountTransactionTemplateType;
import com.freetonleague.core.domain.enums.finance.AccountTransactionType;
import com.freetonleague.core.domain.enums.tournament.TournamentStatusType;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.domain.model.tournament.*;
import com.freetonleague.core.exception.TeamParticipantManageException;
import com.freetonleague.core.exception.config.ExceptionMessages;
import com.freetonleague.core.service.EventService;
import com.freetonleague.core.service.FinancialClientService;
import com.freetonleague.core.service.TeamParticipantService;
import com.freetonleague.core.service.tournament.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

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
    private final TeamParticipantService teamParticipantService;
    private final RestTournamentProposalFacadeImpl restTournamentProposalFacade;

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
        log.debug("^ new status changed for tournament '{}' with new status '{}'.", tournament, newTournamentMatchStatus);
        if (newTournamentMatchStatus.isCreated()) {
            financialClientService.createAccountByHolderInfo(tournament.getCoreId(),
                    AccountHolderType.TOURNAMENT, tournament.getName());
        }
        if (tournament.getAccessType().isPaid() && TournamentStatusType.canceledStatusList.contains(newTournamentMatchStatus)) {
            List<TournamentTeamProposal> activeProposalList = tournamentProposalService.getActiveTeamProposalListByTournament(tournament);
            for (TournamentTeamProposal proposal : activeProposalList) {
                // check if proposal is active and reject/cancel wasn't made earlier
                if (nonNull(proposal.getState()) && ParticipationStateType.activeProposalStateList.contains(proposal.getState())) {
                    proposal.setState(ParticipationStateType.CANCELLED);
                    tournamentProposalService.cancelProposal(proposal);
                }
            }
        }
    }

    /**
     * Process tournament brackets was generated
     */
    @Override
    public void processTournamentBracketsChanged(Tournament tournament) {
        log.debug("^ brackets for tournament.id '{}' was changed. " +
                "Compose and update tournaments setting according to tournament template", tournament.getId());
        tournamentService.composeAdditionalSettings(tournament);
    }

    /**
     * Process match status changing
     */
    @Override
    public void processMatchStatusChange(TournamentMatch tournamentMatch, TournamentStatusType newTournamentMatchStatus) {
        log.debug("^ status of match was changed from '{}' to '{}'. Process match status change in Tournament Event Service.",
                tournamentMatch.getPrevStatus(), newTournamentMatchStatus);

        TournamentSeries tournamentSeries = tournamentMatch.getTournamentSeries();
        Tournament tournament = tournamentSeries.getTournamentRound().getTournament();
        if (newTournamentMatchStatus.isFinished()
                && tournamentMatchService.isAllMatchesFinishedBySeries(tournamentMatch.getTournamentSeries())
                && tournament.getSystemType().isAutoFinishSeriesEnabled()
                && !tournamentSeries.getStatus().isFinished()) {
            log.debug("^ checked all match is finished for series.id {}, tournament system type assume automation ->" +
                    "so we finish the series", tournamentSeries.getId());
            this.handleSeriesStatusChange(tournamentMatch.getTournamentSeries(), TournamentStatusType.FINISHED);
        }
    }

    /**
     * Process series status changing
     */
    @Override
    public void processSeriesStatusChange(TournamentSeries tournamentSeries, TournamentStatusType newTournamentSeriesStatus) {
        log.debug("^ status of series was changed from '{}' to '{}'. Process series status change in Tournament Event Service.",
                tournamentSeries.getPrevStatus(), newTournamentSeriesStatus);

        TournamentRound tournamentRound = tournamentSeries.getTournamentRound();
        Tournament tournament = tournamentRound.getTournament();
        TournamentSettings tournamentSettings = tournament.getTournamentSettings();

        if (newTournamentSeriesStatus.isFinished()) {
            if ((nonNull(tournamentSeries.getPrevStatus()) && !tournamentSeries.getPrevStatus().isFinished())
                    && tournamentSettings.getIsSequentialSeriesEnabled()
                    && !tournamentRound.getIsLast()) {
                log.debug("^ series.id '{}' is finished, prevStatus is '{}', tournament settings IsSequentialSeriesEnabled=true ->" +
                        "so we start composeSequentialSeriesForPrevSeries", tournamentSeries.getId(), tournamentSeries.getPrevStatus());
                tournamentSeriesService.composeSequentialSeriesForPrevSeries(tournamentSeries);
            }
            if (tournamentSeriesService.isAllSeriesFinishedByRound(tournamentSeries.getTournamentRound())
                    && !tournamentRound.getStatus().isFinished()
                    && tournament.getSystemType().isAutoFinishRoundEnabled()) {
                log.debug("^ checked all series is finished for round.id '{}', round is not already finished" +
                        " and tournament system type assume automation -> so we finish the round", tournamentRound.getId());
                this.handleRoundStatusChange(tournamentRound, TournamentStatusType.FINISHED);
            }
        }
    }

    /**
     * Process round status changing
     */
    @Override
    public void processRoundStatusChange(TournamentRound tournamentRound, TournamentStatusType
            newTournamentRoundStatus) {
        log.debug("^ status of round was changed from '{}' to '{}'. Process round status change in Tournament Event Service.",
                tournamentRound.getPrevStatus(), newTournamentRoundStatus);
        Tournament tournament = tournamentRound.getTournament();
        // check if round is finished then we automatically generate new round or finish tournament
        if (newTournamentRoundStatus.isFinished() && tournament.getSystemType().isGenerationRoundEnabled()) {
            // check if round is not last or not all rounds is already finished
            boolean isAllRoundsFinished = !tournamentRoundService.isAllRoundsFinishedByTournament(tournament);
            boolean isLastRound = isTrue(tournamentRound.getIsLast());
            boolean isRoundsNotFinishedInTournament = !isLastRound || !isAllRoundsFinished;
            if (!tournament.getTournamentSettings().getIsSequentialSeriesEnabled() && isRoundsNotFinishedInTournament) {
                tournamentRoundService.composeNextRoundForTournament(tournament);
            } else if (!isRoundsNotFinishedInTournament) {
                // last (all rounds) is finished, so finishing the tournament
                this.handleTournamentStatusChange(tournament, TournamentStatusType.FINISHED);
            }
        }
    }

    /**
     * Process series hasNoWinner or dead head for rivals
     */
    @Override
    public void processSeriesHasNoWinner(TournamentSeries tournamentSeries) {
        log.error("!> We have no winner or a dead head in series '{}'. Create new match manually", tournamentSeries);
    }

    /**
     * Process tournament team proposal status changing
     */
    @Override
    public List<AccountTransactionInfoDto> processTournamentTeamProposalStateChange(TournamentTeamProposal tournamentTeamProposal,
                                                                                    ParticipationStateType newTournamentTeamState) {
        log.debug("^ state of team proposal was changed from '{}' to '{}'. Process team proposal state change in Tournament Event Service.",
                tournamentTeamProposal.getPrevState(), newTournamentTeamState);
        List<AccountTransactionInfoDto> paymentList = null;
        if (tournamentTeamProposal.getTournament().getAccessType().isPaid()) {
            if (this.needToPaidParticipationFee(tournamentTeamProposal)) {
                log.debug("^ state of team proposal '{}' required participation fee purchase. Trying to call withdraw " +
                        "transaction in Tournament Event Service.", tournamentTeamProposal);
                paymentList = this.tryMakeParticipationFeePayment(tournamentTeamProposal);
            } else if (this.needToRefundParticipationFee(tournamentTeamProposal)) {
                log.debug("^ state of team proposal '{}' required refund of participation fee. Trying to call debit " +
                        "transaction in Tournament Event Service.", tournamentTeamProposal);
                paymentList = this.tryMakeParticipationFeeRefund(tournamentTeamProposal, false);
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

    /**
     * Try to make participation fee and commission from team to tournament
     */
    private List<AccountTransactionInfoDto> tryMakeParticipationFeePayment(TournamentTeamProposal teamProposal) {
        log.debug("^ try to make participation fee and commission to tournament from team.id '{}' and teamProposal.id '{}'",
                teamProposal.getTeam().getId(), teamProposal.getId());
        User teamCapitan = teamProposal.getTeam().getCaptain().getUser();
        Tournament tournament = teamProposal.getTournament();

        double teamParticipationFee = tournamentProposalService.calculateTeamParticipationFee(teamProposal);
        AccountInfoDto teamCapitanAccountDto = financialClientService.getAccountByHolderInfo(teamCapitan.getLeagueId(),
                AccountHolderType.USER);
        if (teamCapitanAccountDto.getAmount() < teamParticipationFee) {
            log.warn("~ forbiddenException for create new proposal for team '{}' to tournament id '{}' and status '{}'. " +
                            "Team capitan '{}' doesn't have enough fund to pay participation fee for all team members",
                    teamProposal.getTeam().getId(), tournament.getId(), tournament.getStatus(), teamCapitan);
            throw new TeamParticipantManageException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_VERIFICATION_ERROR,
                    String.format("Team capitan '%s' doesn't have enough fund to pay participation fee for all team members. " +
                            "Request rejected.", teamCapitan));
        }

        double tournamentOwnerCommissionPercentage = tournament.getTournamentSettings().getOrganizerCommission() / 100;
        double commissionAmount = teamParticipationFee * tournamentOwnerCommissionPercentage;
        double tournamentFundAmount = teamParticipationFee - commissionAmount;

        AccountInfoDto tournamentOwnerAccountDto = financialClientService.getAccountByHolderInfo(
                tournament.getCreatedBy().getLeagueId(), AccountHolderType.USER);
        AccountInfoDto tournamentAccountDto = financialClientService.getAccountByHolderInfo(
                tournament.getCoreId(), AccountHolderType.TOURNAMENT);

        List<AccountTransactionInfoDto> participatePaymentList = new ArrayList<>();
        AccountTransactionInfoDto result = financialClientService.applyPurchaseTransaction(
                this.composeParticipationFeeTransaction(teamCapitanAccountDto, tournamentAccountDto, tournamentFundAmount));
        if (isNull(result)) {
            log.warn("~ forbiddenException for create new proposal for team '{}' to tournament id '{}'. " +
                            "Error while transferring fund to pat participation fee. Check requested params.",
                    teamProposal.getTeam().getId(), tournament.getId());
            throw new TeamParticipantManageException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_VERIFICATION_ERROR,
                    "Error while transferring fund to pay participation fee. Check requested params.");
        }
        participatePaymentList.add(result);

        result = financialClientService.applyPurchaseTransaction(
                this.composeParticipationCommissionTransaction(teamCapitanAccountDto, tournamentOwnerAccountDto, commissionAmount));
        if (isNull(result)) {
            log.warn("~ forbiddenException for create new proposal for team '{}' to tournament id '{}'. " +
                            "Error while transferring fund to pat participation fee. Check requested params.",
                    teamProposal.getTeam().getId(), tournament.getId());
            throw new TeamParticipantManageException(ExceptionMessages.TOURNAMENT_TEAM_PROPOSAL_VERIFICATION_ERROR,
                    "Error while transferring fund to pay commission participation fee. Check requested params.");
        }
        participatePaymentList.add(result);
        return participatePaymentList;
    }

    /**
     * Try to make refund of participation fee and commission to team
     */
    private List<AccountTransactionInfoDto> tryMakeParticipationFeeRefund(TournamentTeamProposal
                                                                                  teamProposal, Boolean needToPayPenalty) {
        log.debug("^ try to refund tournament participation fee and commission to team.id '{}' and teamProposal.id '{}'",
                teamProposal.getTeam().getId(), teamProposal.getId());
        // abort transaction
        List<AccountTransactionInfoDto> updatedParticipatePaymentList = teamProposal.getParticipatePaymentList().stream()
                .map(financialClientService::abortTransaction).collect(Collectors.toList());
        if (needToPayPenalty) {
            //implement penalty payments
            log.error("!> penalty payments not implemented with tryMakeParticipationFeeRefund");
        }
        log.debug("^ successfully refund to team.id '{}' and teamProposal.id '{}'", teamProposal.getTeam().getId(), teamProposal.getId());
        return updatedParticipatePaymentList;
    }

    private AccountTransactionInfoDto composeParticipationFeeTransaction(AccountInfoDto accountSourceDto,
                                                                         AccountInfoDto accountTargetDto,
                                                                         double tournamentFundAmount) {
        return AccountTransactionInfoDto.builder()
                .amount(tournamentFundAmount)
                .sourceAccount(accountSourceDto)
                .targetAccount(accountTargetDto)
                .transactionType(AccountTransactionType.PAYMENT)
                .transactionTemplateType(AccountTransactionTemplateType.TOURNAMENT_ENTRANCE_FEE)
                .status(AccountTransactionStatusType.FINISHED)
                .build();
    }

    private AccountTransactionInfoDto composeParticipationCommissionTransaction(AccountInfoDto accountSourceDto,
                                                                                AccountInfoDto accountTargetDto,
                                                                                double commissionAmount) {
        return AccountTransactionInfoDto.builder()
                .amount(commissionAmount)
                .sourceAccount(accountSourceDto)
                .targetAccount(accountTargetDto)
                .transactionType(AccountTransactionType.PAYMENT)
                .transactionTemplateType(AccountTransactionTemplateType.TOURNAMENT_ENTRANCE_COMMISSION)
                .status(AccountTransactionStatusType.FINISHED)
                .build();
    }

    private Map<Long, Tournament> getIdToTournamentMap() {
        return Collections.unmodifiableMap(
                tournamentService.getAllActiveTournament()
                        .stream()
                        .collect(Collectors.toMap(Tournament::getId, tournament -> tournament)));
    }

    private void tryMakeStatusUpdateOperations(Tournament tournament) {
        log.debug("^ try to define events for tournament: '{}'", tournament.getId());
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
        log.debug("^ tournament '{}' with status '{}' were checked, and added to cache", tournament.getName(), tournament.getId());
        cachedTournamentId.add(tournament.getId());
    }

    private void handleTournamentStatusChange(Tournament tournament, TournamentStatusType newTournamentStatus) {
        log.debug("^ handle changing status of tournament to '{}' in Tournament Event Service.", newTournamentStatus);
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
                .createdDate(LocalDate.now())
                .build();
        try {
            eventService.sendEvent(event);
        } catch (Exception exc) {
            log.error("Error in handleStatusChange: '{}'", exc.getMessage());
        }
        //TODO удалить непосредственный вызов изменения данных и разработать обработчик сообщений из Kafka до 01/10/21
        // или удалить коммент
        tournament.setStatus(newTournamentStatus);
        tournamentService.editTournament(tournament);
    }

    private void handleRoundStatusChange(TournamentRound tournamentRound, TournamentStatusType
            newTournamentRoundStatus) {
        log.debug("^ handle changing status of round to '{}' in Tournament Event Service.", newTournamentRoundStatus);
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
                .createdDate(LocalDate.now())
                .build();
        try {
            eventService.sendEvent(event);
        } catch (Exception exc) {
            log.error("Error in handleStatusChange: '{}'", exc.getMessage());
        }
        // TODO remove direct data change invocation and message designer from Kafka before 01/10/21
        //or remove comment
        tournamentRound.setStatus(newTournamentRoundStatus);
        tournamentRoundService.editRound(tournamentRound);
    }

    private void handleSeriesStatusChange(TournamentSeries tournamentSeries, TournamentStatusType
            newTournamentSeriesStatus) {
        log.debug("^ handle changing status of series to '{}' in Tournament Event Service.", newTournamentSeriesStatus);
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
                .createdDate(LocalDate.now())
                .build();
        try {
            eventService.sendEvent(event);
        } catch (Exception exc) {
            log.error("Error in handleStatusChange: '{}'", exc.getMessage());
        }

        // TODO remove direct data change invocation and message designer from Kafka before 01/10/21
        //or remove comment
        tournamentSeries.setStatus(newTournamentSeriesStatus);
        tournamentSeriesService.editSeries(tournamentSeries);
    }

    public void handleMatchStatusChange(TournamentMatch tournamentMatch, TournamentStatusType
            newTournamentMatchStatus) {
        log.debug("^ handle changing status of match to '{}' in Tournament Event Service.", newTournamentMatchStatus);
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
                .createdDate(LocalDate.now())
                .build();
        try {
            eventService.sendEvent(event);
        } catch (Exception exc) {
            log.error("Error in handleStatusChange: '{}'", exc.getMessage());
        }
    }
}
