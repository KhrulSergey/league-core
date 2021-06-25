package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.AccountTransactionInfoDto;
import com.freetonleague.core.domain.enums.ParticipationStateType;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.model.*;

import java.util.List;


public interface TournamentEventService {

    /**
     * Process tournament status changing
     */
    void processTournamentStatusChange(Tournament tournament, TournamentStatusType newTournamentMatchStatus);

    /**
     * Process tournament brackets was generated
     */
    void processTournamentBracketsChanged(Tournament tournament);

    /**
     * Process match status changing
     */
    void processMatchStatusChange(TournamentMatch tournamentMatch, TournamentStatusType newTournamentMatchStatus);

    /**
     * Process series status changing
     */
    void processSeriesStatusChange(TournamentSeries tournamentSeries, TournamentStatusType newTournamentSeriesStatus);

    /**
     * Process round status changing
     */
    void processRoundStatusChange(TournamentRound tournamentRound, TournamentStatusType newTournamentRoundStatus);

    /**
     * Process series dead head for rivals
     */
    void processSeriesDeadHead(TournamentSeries tournamentSeries);

    /**
     * Process tournament team proposal status changing
     */
    List<AccountTransactionInfoDto> processTournamentTeamProposalStateChange(TournamentTeamProposal tournamentTeamProposal,
                                                                             ParticipationStateType newTournamentTeamState);
}
