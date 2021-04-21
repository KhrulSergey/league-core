package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.enums.TournamentTeamStateType;
import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.Tournament;
import com.freetonleague.core.domain.model.TournamentTeamParticipant;
import com.freetonleague.core.domain.model.TournamentTeamProposal;
import com.freetonleague.core.repository.TournamentTeamParticipantRepository;
import com.freetonleague.core.repository.TournamentTeamProposalRepository;
import com.freetonleague.core.service.TournamentService;
import com.freetonleague.core.service.TournamentTeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class TournamentTeamServiceImpl implements TournamentTeamService {

    private final TournamentTeamProposalRepository teamProposalRepository;
    private final TournamentTeamParticipantRepository tournamentTeamParticipantRepository;
    private final TournamentService tournamentService;

    private final List<TournamentTeamStateType> activeProposalStateList = List.of(
            TournamentTeamStateType.APPROVE,
            TournamentTeamStateType.CREATED
    );

    private final List<TournamentTeamStateType> disabledProposalStateList = List.of(
            TournamentTeamStateType.REJECT,
            TournamentTeamStateType.CANCELLED
    );

    /**
     * Returns tournament team proposal (request to participate on tournament) by id.
     */
    @Override
    public TournamentTeamProposal getProposalById(long id) {
        log.debug("^ trying to get team proposal by id {}", id);
        return teamProposalRepository.findById(id).orElse(null);
    }

    /**
     * Returns tournament team proposal (request to participate on tournament) by team and tournament.
     */
    @Override
    public TournamentTeamProposal getProposalByTeamAndTournament(Team team, Tournament tournament) {
        if (isNull(team) || isNull(tournament)) {
            log.error("!> requesting getProposalByTeamAndTournament for NULL team {} or NULL tournament {}. Check evoking clients",
                    team, tournament);
            return null;
        }
        log.debug("^ trying to get tournament team proposal list for team: {} and tournament {}",
                team.getId(), tournament.getId());
        return teamProposalRepository.findByTeamAndTournament(team, tournament);
    }

    /**
     * Returns list of all tournament team proposal filtered by requested params
     */
    @Override
    public Page<TournamentTeamProposal> getProposalListForTournament(Pageable pageable, Tournament tournament) {
        if (isNull(pageable) || isNull(tournament)) {
            log.error("!> requesting getTournamentList for NULL pageable {} or NULL tournament {}. Check evoking clients",
                    pageable, tournament);
            return null;
        }
        List<TournamentTeamStateType> filteredProposalStateList = List.of(TournamentTeamStateType.values());
        log.debug("^ trying to get tournament team proposal list with pageable params: {} and for tournament {}",
                pageable, tournament.getId());
        return teamProposalRepository.findAllByTournamentAndStateIn(pageable, tournament, filteredProposalStateList);
    }

    /**
     * Returns tournament team proposal (request to participate on tournament).
     */
    @Override
    public TournamentTeamProposal addProposal(TournamentTeamProposal tournamentTeamProposal) {
        if (isNull(tournamentTeamProposal)) {
            log.error("!> requesting addProposal for NULL tournamentTeamProposal. Check evoking clients");
            return null;
        }
        log.debug("^ trying to add new team proposal {}", tournamentTeamProposal);

        TournamentTeamProposal saved = teamProposalRepository.save(tournamentTeamProposal);
        this.handleTournamentStateChanged(saved);
        return saved;
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
        if (!this.isExistsTournamentTeamParticipantById(tournamentTeamProposal.getId())) {
            log.error("!> requesting modify tournament team proposal {} for non-existed tournament team proposal. Check evoking clients",
                    tournamentTeamProposal.getId());
            return null;
        }
        log.debug("^ trying to modify tournament team proposal {}", tournamentTeamProposal);
        TournamentTeamProposal saved = teamProposalRepository.save(tournamentTeamProposal);
        this.handleTournamentStateChanged(saved);
        return saved;
    }

    /**
     * Quit requested team (in team proposal) from tournament.
     * TournamentTeamProposal marked as CANCELLED
     */
    @Override
    public TournamentTeamProposal quitFromTournament(TournamentTeamProposal tournamentTeamProposal) {
        if (isNull(tournamentTeamProposal) || isNull(tournamentTeamProposal.getTournament())) {
            log.error("!> requesting addProposal for NULL tournamentTeamProposal {} or embedded Tournament {}. Check evoking clients", tournamentTeamProposal, tournamentTeamProposal.getTournament());
            return null;
        }
        if (!tournamentService.getTournamentActiveStatusList().contains(tournamentTeamProposal.getTournament().getStatus())) {
            log.error("!> requesting quitFromTournament for tournament that is not active. Check evoking clients");
            return null;
        }
        log.debug("^ trying to quit team with proposal {} form tournament", tournamentTeamProposal);
        tournamentTeamProposal.setState(TournamentTeamStateType.CANCELLED);

        TournamentTeamProposal saved = teamProposalRepository.save(tournamentTeamProposal);
        this.handleTournamentStateChanged(saved);
        return saved;
    }

    /**
     * Returns list of approved team proposal list for specified tournament.
     */
    @Override
    public List<TournamentTeamProposal> getActiveTeamProposalListByTournament(Tournament tournament) {
        if (isNull(tournament)) {
            log.error("!> requesting getActiveTeamProposalByTournament for NULL tournament. Check evoking clients");
            return null;
        }
        log.debug("^ trying to get Approved team proposal list by tournament with id: {}", tournament.getId());

        return teamProposalRepository.findAllByTournamentAndState(tournament, TournamentTeamStateType.APPROVE);
    }

    /**
     * Returns "started" statuses for tournaments
     */
    @Override
    public List<TournamentTeamStateType> getTournamentTeamProposalActiveStateList() {
        return activeProposalStateList;
    }

    /**
     * Returns founded participant by id
     */
    @Override
    public TournamentTeamParticipant getTournamentTeamParticipantById(long id) {
        log.debug("^ trying to get tournament team participant by id: {}", id);
        return tournamentTeamParticipantRepository.findById(id).orElse(null);
    }

    private boolean isExistsTournamentTeamParticipantById(long id) {
        return teamProposalRepository.existsById(id);
    }

    /**
     * Prototype for handle tournament team proposal state
     */
    private void handleTournamentStateChanged(TournamentTeamProposal tournamentTeamProposal) {
        log.warn("~ status for tournament team proposal id {} was changed from {} to {} ",
                tournamentTeamProposal.getId(), tournamentTeamProposal.getPrevState(), tournamentTeamProposal.getState());
        // TODO Call bank service to make partial refund

        List<TournamentTeamStateType> teamProposalStateList = new ArrayList<>(disabledProposalStateList);
        teamProposalStateList.add(TournamentTeamStateType.CREATED);

        // if prev status was non-active and new status is active we need to debit money from team
        if ((isNull(tournamentTeamProposal.getPrevState()) || teamProposalStateList.contains(tournamentTeamProposal.getPrevState()))
                && activeProposalStateList.contains(tournamentTeamProposal.getState())) {
            this.bankServiceDebitMockMethod(tournamentTeamProposal);
        } else
            // if prev status was active and new status is non-active we need to refund money to team
            if (nonNull(tournamentTeamProposal.getPrevState()) && activeProposalStateList.contains(tournamentTeamProposal.getPrevState()) &&
                    teamProposalStateList.contains(tournamentTeamProposal.getState())) {
                this.bankServiceRefundMockMethod(tournamentTeamProposal);
            }
        tournamentTeamProposal.setPrevState(tournamentTeamProposal.getState());
    }

    //TODO delete method
    private void bankServiceRefundMockMethod(TournamentTeamProposal teamProposal) {
        //make some staff
    }

    //TODO delete method
    private void bankServiceDebitMockMethod(TournamentTeamProposal teamProposal) {
        //make some staff
    }
}
