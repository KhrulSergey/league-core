package com.freetonleague.core.service;


import com.freetonleague.core.domain.model.TournamentTeamParticipant;
import com.freetonleague.core.domain.model.TournamentTeamProposal;

import java.util.List;

public interface TournamentTeamService {

    TournamentTeamProposal addProposal(TournamentTeamProposal tournamentTeamProposal);

    List<TournamentTeamParticipant> getTournamentTeamParticipant(TournamentTeamProposal tournamentTeamProposal);

    void quitFromTournament(TournamentTeamProposal tournamentTeamProposal);
}
