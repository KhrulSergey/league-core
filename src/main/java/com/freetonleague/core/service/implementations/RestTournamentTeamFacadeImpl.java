package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.TeamDto;
import com.freetonleague.core.domain.dto.TournamentTeamParticipantDto;
import com.freetonleague.core.domain.dto.TournamentTeamProposalDto;
import com.freetonleague.core.domain.enums.TournamentTeamParticipantStatusType;
import com.freetonleague.core.domain.enums.TournamentTeamStateType;
import com.freetonleague.core.domain.enums.TournamentTeamType;
import com.freetonleague.core.domain.model.TournamentTeamProposal;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.mapper.TournamentTeamMapper;
import com.freetonleague.core.service.RestTeamFacade;
import com.freetonleague.core.service.RestTeamParticipantFacade;
import com.freetonleague.core.service.RestTournamentTeamFacade;
import com.freetonleague.core.service.TournamentTeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service-facade for managing tournament team proposal and team composition
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RestTournamentTeamFacadeImpl implements RestTournamentTeamFacade {

    private final RestTeamFacade restTeamFacade;
    private final RestTeamParticipantFacade restTeamParticipantFacade;
    private final TournamentTeamService tournamentTeamService;

    private final TournamentTeamMapper tournamentTeamMapper;
    private final Validator validator;


    /**
     * Registry new team to tournament
     */
    @Override
    public TournamentTeamProposalDto createProposalToTournament(long tournamentId, long teamId, TournamentTeamProposalDto teamProposalDto, User user) {

        tournamentTeamService.addProposal(tournamentTeamMapper.fromDto(teamProposalDto));

        TeamDto teamDto = (TeamDto) restTeamFacade.getTeamById(teamId, user);

        List<TournamentTeamParticipantDto> tournamentTeamParticipantDtoList =
                restTeamFacade.getVerifiedTeamById(teamDto.getId(), user).getParticipantList().parallelStream()
                        .map(teamParticipant ->
                                TournamentTeamParticipantDto.builder()
                                        .statusInProposal(TournamentTeamParticipantStatusType.values()[(int) Math.round(Math.random() * (TournamentTeamParticipantStatusType.values().length - 1))])
                                        .teamParticipantId(teamParticipant.getId())
                                        .tournamentTeamProposalId(1L)
                                        .userLeagueId(teamParticipant.getUser().getLeagueId().toString())
                                        .build()
                        ).collect(Collectors.toList());

        TournamentTeamProposalDto tournamentTeamProposalDto = TournamentTeamProposalDto.builder()
                .teamId(teamId)
                .status(TournamentTeamStateType.values()[(int) Math.round(Math.random() * (TournamentTeamStateType.values().length - 1))])
                .type(TournamentTeamType.values()[(int) Math.round(Math.random() * (TournamentTeamType.values().length - 1))])
                .tournamentTeamParticipantList(tournamentTeamParticipantDtoList)
                .build();

        return tournamentTeamProposalDto;
    }

    /**
     * Quit team from tournament
     */
    @Override
    public void quitFromTournament(long tournamentId, long teamId, User user) {
        tournamentTeamService.quitFromTournament(new TournamentTeamProposal());
        return;
    }
}


