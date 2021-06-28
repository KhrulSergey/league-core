package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.AccountTransactionInfoDto;
import com.freetonleague.core.domain.enums.ParticipationStateType;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.model.*;
import com.freetonleague.core.repository.TournamentTeamParticipantRepository;
import com.freetonleague.core.repository.TournamentTeamProposalRepository;
import com.freetonleague.core.service.TournamentEventService;
import com.freetonleague.core.service.TournamentProposalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Slf4j
@RequiredArgsConstructor
@Service
public class TournamentProposalServiceImpl implements TournamentProposalService {

    private final TournamentTeamProposalRepository teamProposalRepository;
    private final TournamentTeamParticipantRepository tournamentTeamParticipantRepository;

    @Lazy
    @Autowired
    private TournamentEventService tournamentEventService;

    /**
     * Returns tournament team proposal (request to participate on tournament) by id.
     */
    @Override
    public TournamentTeamProposal getProposalById(long id) {
        log.debug("^ trying to get team proposal by id '{}'", id);
        return teamProposalRepository.findById(id).orElse(null);
    }

    /**
     * Returns tournament team proposal (request to participate on tournament) by team and tournament.
     */
    @Override
    public TournamentTeamProposal getProposalByTeamAndTournament(Team team, Tournament tournament) {
        if (isNull(team) || isNull(tournament)) {
            log.error("!> requesting getProposalByTeamAndTournament for NULL team '{}' or NULL tournament '{}'. Check evoking clients",
                    team, tournament);
            return null;
        }
        log.debug("^ trying to get tournament team proposal for team: '{}' and tournament '{}'",
                team.getId(), tournament.getId());
        return teamProposalRepository.findByTeamAndTournament(team, tournament);
    }

    /**
     * Returns tournament team proposal (request to participate on tournament) by capitan of team and tournament.
     */
    @Override
    public List<TournamentTeamProposal> getProposalByCapitanUserAndTournament(User userCapitan, Tournament tournament) {
        if (isNull(userCapitan) || isNull(tournament)) {
            log.error("!> requesting getProposalByTeamAndTournament for NULL userCapitan '{}' or NULL tournament '{}'. Check evoking clients",
                    userCapitan, tournament);
            return null;
        }
        log.debug("^ trying to get tournament proposal for userCapitan.id: '{}' and tournament '{}'",
                userCapitan.getLeagueId(), tournament.getId());
        return teamProposalRepository.findProposalByUserCapitanAndTournament(userCapitan, tournament);
    }

    /**
     * Returns list of all tournament team proposal filtered by requested params
     */
    @Override
    public Page<TournamentTeamProposal> getProposalListForTournament(Pageable pageable, Tournament tournament,
                                                                     List<ParticipationStateType> stateList) {
        if (isNull(pageable) || isNull(tournament)) {
            log.error("!> requesting getTournamentList for NULL pageable '{}' or NULL tournament '{}'. Check evoking clients",
                    pageable, tournament);
            return null;
        }

        List<ParticipationStateType> filteredProposalStateList = isNotEmpty(stateList) ? stateList
                : List.of(ParticipationStateType.values());
        log.debug("^ trying to get tournament team proposal list with pageable params: '{}' and for tournament.id '{}' and stateList '{}'",
                pageable, tournament.getId(), filteredProposalStateList);
        return teamProposalRepository.findAllByTournamentAndStateIn(pageable, tournament, filteredProposalStateList);
    }

    /**
     * Returns tournament team proposal (request to participate on tournament).
     */
    @Override
    public TournamentTeamProposal addProposal(TournamentTeamProposal tournamentTeamProposal) {
        if (isNull(tournamentTeamProposal) || isNull(tournamentTeamProposal.getTournament())) {
            log.error("!> requesting addProposal for NULL teamProposal '{}' or NULL teamProposal.tournament. Check evoking clients",
                    tournamentTeamProposal);
            return null;
        }
        log.debug("^ trying to add new team proposal '{}'", tournamentTeamProposal);

        List<AccountTransactionInfoDto> paymentList = tournamentEventService.processTournamentTeamProposalStateChange(
                tournamentTeamProposal, tournamentTeamProposal.getState());
        tournamentTeamProposal.setParticipatePaymentList(paymentList);
        return teamProposalRepository.save(tournamentTeamProposal);
    }

    /**
     * Edit tournament team proposal in DB.
     */
    @Override
    public TournamentTeamProposal editProposal(TournamentTeamProposal tournamentTeamProposal) {
        if (isNull(tournamentTeamProposal)) {
            log.error("!> requesting modify tournament team proposal with editProposal for NULL tournamentTeamProposal. Check evoking clients");
            return null;
        }
        if (!this.isExistsTournamentTeamProposalById(tournamentTeamProposal.getId())) {
            log.error("!> requesting modify tournament team proposal for non-existed tournament team proposal.id '{}'. Check evoking clients",
                    tournamentTeamProposal.getId());
            return null;
        }
        log.debug("^ trying to modify tournament team proposal '{}'", tournamentTeamProposal);
        if (tournamentTeamProposal.isStateChanged()) {
            this.handleTeamProposalStateChanged(tournamentTeamProposal);
        }
        return teamProposalRepository.save(tournamentTeamProposal);
    }

    /**
     * Quit requested team (in team proposal) from tournament.
     * TournamentTeamProposal marked as CANCELLED
     */
    @Override
    public TournamentTeamProposal quitFromTournament(TournamentTeamProposal tournamentTeamProposal) {
        if (isNull(tournamentTeamProposal) || isNull(tournamentTeamProposal.getTournament())) {
            log.error("!> requesting addProposal for NULL tournamentTeamProposal '{}' or embedded Tournament '{}'. Check evoking clients",
                    tournamentTeamProposal, tournamentTeamProposal != null ? tournamentTeamProposal.getTournament() : null);
            return null;
        }
        if (!TournamentStatusType.activeStatusList.contains(tournamentTeamProposal.getTournament().getStatus())) {
            log.error("!> requesting quitFromTournament for tournament that is not active. Check evoking clients");
            return null;
        }
        log.debug("^ trying to quit team with proposal '{}' form tournament", tournamentTeamProposal);
        tournamentTeamProposal.setState(ParticipationStateType.CANCELLED);

        if (tournamentTeamProposal.isStateChanged()) {
            this.handleTeamProposalStateChanged(tournamentTeamProposal);
        }
        return teamProposalRepository.save(tournamentTeamProposal);
    }

    /**
     * Returns list of approved team proposal list for specified tournament.
     */
    @Override
    public List<TournamentTeamProposal> getApprovedTeamProposalListByTournament(Tournament tournament) {
        if (isNull(tournament)) {
            log.error("!> requesting getActiveTeamProposalByTournament for NULL tournament. Check evoking clients");
            return null;
        }
        log.debug("^ trying to get Approved team proposal list by tournament with id: '{}'", tournament.getId());

        return teamProposalRepository.findAllByTournamentAndState(tournament, ParticipationStateType.APPROVE);
    }


    /**
     * Returns list of active team proposal list for specified tournament.
     */
    @Override
    public List<TournamentTeamProposal> getActiveTeamProposalListByTournament(Tournament tournament) {
        if (isNull(tournament)) {
            log.error("!> requesting getActiveTeamProposalByTournament for NULL tournament. Check evoking clients");
            return null;
        }
        log.debug("^ trying to get Approved team proposal list by tournament with id: '{}'", tournament.getId());

        return teamProposalRepository.findAllByTournamentAndStateIn(tournament,
                ParticipationStateType.activeProposalStateList);
    }

    /**
     * Returns founded participant by id
     */
    @Override
    public TournamentTeamParticipant getTournamentTeamParticipantById(long id) {
        log.debug("^ trying to get tournament team participant by id: '{}'", id);
        return tournamentTeamParticipantRepository.findById(id).orElse(null);
    }

    /**
     * Returns list of discordId from user that specified in tournamentTeamProposal
     */
    @Override
    public Set<String> getUserDiscordIdListFromTeamProposal(TournamentTeamProposal tournamentTeamProposal) {
        if (isNull(tournamentTeamProposal)) {
            log.error("!> requesting getUserDiscordIdListFromTeamProposal for NULL tournamentTeamProposal. Check evoking clients");
            return null;
        }
        return tournamentTeamParticipantRepository.findUserDiscordIdListForTournamentTeamProposal(tournamentTeamProposal);
    }

    /**
     * Returns calculated participation fee for specified teamProposal
     */
    @Override
    public double calculateTeamParticipationFee(TournamentTeamProposal teamProposal) {
        int tournamentTeamParticipantCount = teamProposal.getTournamentTeamParticipantList().size();
        double participationFee = teamProposal.getTournament().getTournamentSettings().getParticipationFee();
        return participationFee * tournamentTeamParticipantCount;
    }

    private boolean isExistsTournamentTeamProposalById(long id) {
        return teamProposalRepository.existsById(id);
    }

    /**
     * Prototype for handle tournament team proposal state
     */
    private void handleTeamProposalStateChanged(TournamentTeamProposal tournamentTeamProposal) {
        log.debug("~ status for tournament team proposal id '{}' was changed from '{}' to '{}' ",
                tournamentTeamProposal.getId(), tournamentTeamProposal.getPrevState(), tournamentTeamProposal.getState());
        //TODO unlock auto-payment in processTournamentTeamProposalStateChange when auto-refund will be ready
//        tournamentEventService.processTournamentTeamProposalStateChange(tournamentTeamProposal, tournamentTeamProposal.getState());
        tournamentTeamProposal.setPrevState(tournamentTeamProposal.getState());
    }
}
