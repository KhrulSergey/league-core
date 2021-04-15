package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.model.TournamentTeamParticipant;
import com.freetonleague.core.domain.model.TournamentTeamProposal;
import com.freetonleague.core.repository.TournamentTeamParticipantRepository;
import com.freetonleague.core.repository.TournamentTeamProposalRepository;
import com.freetonleague.core.service.TeamParticipantService;
import com.freetonleague.core.service.TeamService;
import com.freetonleague.core.service.TournamentService;
import com.freetonleague.core.service.TournamentTeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TournamentTeamServiceImpl implements TournamentTeamService {

    private final TournamentTeamProposalRepository teamProposalRepository;
    private final TournamentTeamParticipantRepository tournamentTeamParticipantRepository;
    private final TournamentService tournamentService;
    private final TeamParticipantService teamParticipantService;
    private final TeamService teamService;

    /**
     * Returns tournament team proposal (request to participate on tournament).
     */
    @Override
    public TournamentTeamProposal addProposal(TournamentTeamProposal tournamentTeamProposal) {
        return null;
    }

    /**
     * Returns founded participant by id
     */
    @Override
    public TournamentTeamParticipant getTournamentTeamParticipantById(long id) {
        log.debug("^ trying to get tournament team participant by id: {}", id);
        return tournamentTeamParticipantRepository.findById(id).orElse(null);
    }

    /**
     * Quit requested team (in team proposal) from tournament.
     */
    @Override
    public void quitFromTournament(TournamentTeamProposal tournamentTeamProposal) {
        //    нельзя удалить команду которая участвует в турнире
    }
}
