package com.freetonleague.core.service.implementations;


import com.freetonleague.core.domain.enums.TournamentMatchSeriesType;
import com.freetonleague.core.domain.enums.TournamentSeriesType;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.model.*;
import com.freetonleague.core.exception.CustomUnexpectedException;
import com.freetonleague.core.exception.ExceptionMessages;
import com.freetonleague.core.service.TournamentGenerator;
import com.freetonleague.core.service.TournamentService;
import com.freetonleague.core.service.TournamentTeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Slf4j
@RequiredArgsConstructor
@Component("singleEliminationGenerator")
public class TournamentSingleEliminationGeneratorImpl implements TournamentGenerator {

    private final TournamentTeamService tournamentTeamService;

    @Lazy
    @Autowired
    private TournamentService tournamentService;

    /**
     * Returns random number for specified total size
     */
    private static int randomGenerator(int totalSize) {
        Random rand = new Random();
        return rand.nextInt(totalSize);
    }

    /**
     * Generate tournament series list with embedded list of match prototypes for specified tournament and it's settings.
     * Tournament should be with empty series list.
     */
    @Override
    public List<TournamentSeries> generateSeriesForTournament(Tournament tournament) {
        if (!tournamentService.getTournamentActiveStatusList().contains(tournament.getStatus())) {
            log.error("!> requesting generate tournament series for non-active tournament. Check evoking clients");
            return null;
        }
        if (!tournament.getTournamentSeriesList().isEmpty()) {
            log.error("!> requesting generate tournament series for tournament with non-empty Series list. Check evoking clients");
            return null;
        }
        GameDisciplineSettings gameDisciplineSettings = tournament.getGameDisciplineSettings();
        TournamentSettings tournamentSettings = tournament.getTournamentSettings();
        List<TournamentTeamProposal> teamProposalList = tournamentTeamService.getActiveTeamProposalByTournament(tournament);
        Integer matchRivalCountLimit = gameDisciplineSettings.getMatchRivalCount();

        //TODO delete mocking of data (getMinTeamCount()) to production
        int teamProposalListSize = !teamProposalList.isEmpty() ?
                teamProposalList.size() :
                tournamentSettings.getMinTeamCount();

        if (!this.isPowerOfN(teamProposalListSize, matchRivalCountLimit)) {
            log.error("!> requesting generate tournament series for tournament with non-acceptable '{}' count of rivals" +
                    " (approved team proposals). Check evoking clients", teamProposalListSize);
            return null;
        }

        int matchCount;
        int currentSeriesSequencePosition = 1;
        List<Set<TournamentTeamProposal>> rivalCombinations;
        List<TournamentSeries> newTournamentSeriesList = new ArrayList<>();

        //generate series and matches
        //for series # 2+ matches will be only prototypes, they will be filled with rivals as soon as first series finish
        do {
            // count matches based upon discipline/tournament settings and teamProposalList size
            matchCount = this.calculateMatchCountForSeries(teamProposalListSize, matchRivalCountLimit);
            // get rival combinations or empty list for 2+ round
            rivalCombinations = this.composeRivalCombinationsFromTeamProposal(matchCount,
                    matchRivalCountLimit, teamProposalList);
            TournamentSeries newSeries = this.generateSeries(rivalCombinations, currentSeriesSequencePosition);
            newSeries.setTournament(tournament);
            newTournamentSeriesList.add(newSeries);
            teamProposalListSize = newSeries.getMatchList().size();
            currentSeriesSequencePosition++;

        } while (matchCount > 1);

        return newTournamentSeriesList;
    }

    /**
     * Returns generated series for specified rivalCombinations, matchCount, seriesPosition
     */
    private TournamentSeries generateSeries(List<Set<TournamentTeamProposal>> rivalCombinations,
                                            Integer seriesPosition) {
        if (isNull(rivalCombinations)) {
            log.error("!> round generation with TournamentSingleEliminationGeneratorImpl caused error. RivalCombinations is NULL. Check evoking params");
            throw new CustomUnexpectedException(ExceptionMessages.TOURNAMENT_SERIES_GENERATION_ERROR);
        }
        TournamentSeries tournamentSeries = TournamentSeries.builder()
                .name("Seria #" + seriesPosition)
                .seriesSequencePosition(seriesPosition)
                .goalMatchCount(rivalCombinations.size())
                .goalMatchRivalCount(rivalCombinations.size())
                .status(TournamentStatusType.CREATED)
                .type(TournamentSeriesType.DEFAULT)
                .build();

        // size of rivalCombinations equals match count
        List<TournamentMatch> tournamentMatchList = rivalCombinations.parallelStream()
                .map(combination -> this.generateMatch(tournamentSeries, combination))
                .collect(Collectors.toList());

        tournamentSeries.setMatchList(tournamentMatchList);
        return tournamentSeries;
    }

    /**
     * Returns new match from specified series and rivalCombination
     */
    private TournamentMatch generateMatch(TournamentSeries tournamentSeries,
                                          Set<TournamentTeamProposal> rivalCombination) {
        TournamentMatch tournamentMatch = TournamentMatch.builder()
                .tournamentSeries(tournamentSeries)
                .name("Match for " + tournamentSeries.getName())
                .status(TournamentStatusType.CREATED)
                .typeForSeries(TournamentMatchSeriesType.UPPER_BRACKET)
                .build();
        Set<TournamentMatchRival> tournamentMatchRivalList = new HashSet<>();
        // create and collect match rivals from specified rival combinations
        if (!rivalCombination.isEmpty()) {
            // get rival combination for one match and create rivals
            rivalCombination.parallelStream()
                    .forEach(teamProposal -> {
                        // add new match rival to list
                        tournamentMatchRivalList.add(this.generateMatchRival(tournamentMatch, teamProposal));
                    });
        }
        tournamentMatch.setRivals(tournamentMatchRivalList);
        return tournamentMatch;
    }

    /**
     * Returns new rival from specified match and team proposal
     */
    private TournamentMatchRival generateMatchRival(TournamentMatch tournamentMatch, TournamentTeamProposal teamProposal) {
        TournamentMatchRival matchRival = TournamentMatchRival.builder()
                .status(TournamentStatusType.CREATED)
                .tournamentMatch(tournamentMatch)
                .teamProposal(teamProposal)
                .build();
        // set rival participants from Main participants of Tournament Team Proposal
        matchRival.setRivalParticipantsFromTournamentTeamParticipant(
                teamProposal.getMainTournamentTeamParticipantList());
        return matchRival;
    }

    /**
     * Returns match count for requested parameters of series and primary team count (team proposals)
     */
    private int calculateMatchCountForSeries(int teamProposalListSize, Integer matchRivalsCount) {
        // team count divide by count of rival per match
        return (teamProposalListSize / matchRivalsCount);
    }

    /**
     * Returns collection of random generated rival teams (TournamentTeamProposal) for further processing
     */
    private List<Set<TournamentTeamProposal>> composeRivalCombinationsFromTeamProposal(Integer rivalCombinationCount,
                                                                                       Integer matchMaxRivalCount,
                                                                                       List<TournamentTeamProposal> teamProposalList) {
        if (isNull(rivalCombinationCount) || isNull(matchMaxRivalCount) || isNull(teamProposalList)) {
            log.error("!> requesting generate rival combination for NULL rivalCombinationCount {} " +
                            "or NULL matchRivalCount {} or NULL teamProposalList {}. Check evoking clients",
                    rivalCombinationCount, matchMaxRivalCount, teamProposalList);
            return null;
        }

        List<Set<TournamentTeamProposal>> rivalCombinationList = new ArrayList<>(rivalCombinationCount);
        for (int i = 0; i < rivalCombinationCount; i++) {
            Set<TournamentTeamProposal> teamProposalSet = new HashSet<>(matchMaxRivalCount);
            // collect one combination of rivals with specified matchRivalCount
            for (int j = 0; j < matchMaxRivalCount; j++) {
                if (teamProposalList.isEmpty()) {
                    break;
                }
                int randomTeam = randomGenerator(teamProposalList.size());
                TournamentTeamProposal teamProposal = teamProposalList.get(randomTeam);
                //removing chosen team proposal
                teamProposalList.remove(teamProposal);
                teamProposalSet.add(teamProposal);
            }
            rivalCombinationList.add(teamProposalSet);
        }
        return rivalCombinationList;
    }

    /**
     * Returns sign if 'value' is power of specified number N
     */
    private boolean isPowerOfN(int value, int N) {
        if (value == 0 || value < N) {
            return false;
        }
        while (value % N == 0) {
            value /= N;
        }
        return value == 1;
    }

}
