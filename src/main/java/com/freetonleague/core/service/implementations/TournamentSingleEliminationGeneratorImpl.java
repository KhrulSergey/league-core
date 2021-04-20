package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.enums.TournamentMatchRivalParticipantStatusType;
import com.freetonleague.core.domain.enums.TournamentRoundType;
import com.freetonleague.core.domain.enums.TournamentSeriesBracketType;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.model.*;
import com.freetonleague.core.exception.CustomUnexpectedException;
import com.freetonleague.core.exception.ExceptionMessages;
import com.freetonleague.core.service.TournamentGenerator;
import com.freetonleague.core.service.TournamentService;
import com.freetonleague.core.service.TournamentTeamService;
import com.freetonleague.core.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
     * Generate tournament round list with embedded list of series & matches prototypes for specified tournament and it's settings.
     * Tournament should be with empty series list.
     */
    @Override
    public List<TournamentRound> generateRoundsForTournament(Tournament tournament) {
        if (!tournamentService.getTournamentActiveStatusList().contains(tournament.getStatus())) {
            log.error("!> requesting generate tournament series for non-active tournament. Check evoking clients");
            return null;
        }
        if (!tournament.getTournamentRoundList().isEmpty()) {
            log.error("!> requesting generate tournament round for tournament with non-empty Round list. Check evoking clients");
            return null;
        }
        GameDisciplineSettings gameDisciplineSettings = tournament.getGameDisciplineSettings();
        TournamentSettings tournamentSettings = tournament.getTournamentSettings();
        List<TournamentTeamProposal> teamProposalList = tournamentTeamService.getActiveTeamProposalByTournament(tournament);
        Integer matchRivalCountLimit = gameDisciplineSettings.getMatchRivalCount();
        Integer matchCountPerSeries = tournamentSettings.getMatchCountPerSeries();

        //TODO delete mocking of data (getMinTeamCount()) to production
        int teamProposalListSize = !teamProposalList.isEmpty() ?
                teamProposalList.size() :
                tournamentSettings.getMinTeamCount();

        if (!this.isPowerOfN(teamProposalListSize, matchRivalCountLimit)) {
            log.error("!> requesting generate tournament round for tournament with non-acceptable '{}' count of rivals" +
                    " (approved team proposals). Check evoking clients", teamProposalListSize);
            return null;
        }

        int roundsCount;
        int currentRoundNumber = 1;
        List<Set<TournamentTeamProposal>> rivalCombinations;
        List<TournamentSeries> parentTournamentSeriesList = new ArrayList<>();
        List<TournamentRound> newTournamentRoundList = new ArrayList<>();

        //generate round, series and matches
        //for round #2+ series(and inner matches) will be only prototypes, they will be filled with rivals as soon as first series finish
        do {
            // count series based upon discipline/tournament settings and teamProposalList size
            roundsCount = this.calculateSeriesCountForRound(teamProposalListSize, matchRivalCountLimit);
            // get rival combinations or empty list for 2+ round
            rivalCombinations = this.composeRivalCombinationsFromTeamProposal(roundsCount,
                    matchRivalCountLimit, teamProposalList);
            TournamentRound newRound = this.generateRound(rivalCombinations, parentTournamentSeriesList, currentRoundNumber, matchCountPerSeries);
            newRound.setTournament(tournament);
            newTournamentRoundList.add(newRound);
            parentTournamentSeriesList = newRound.getSeriesList();
            teamProposalListSize = newRound.getSeriesList().size();
            currentRoundNumber++;

        } while (roundsCount > 1);

        return newTournamentRoundList;
    }

    /**
     * Returns generated series for specified rivalCombinations, matchCount, seriesPosition
     */
    private TournamentRound generateRound(List<Set<TournamentTeamProposal>> rivalCombinations,
                                          List<TournamentSeries> parentTournamentSeriesList,
                                          Integer roundNumber, int matchCountPerSeries) {
        if (isNull(rivalCombinations)) {
            log.error("!> round generation with TournamentSingleEliminationGeneratorImpl caused error. RivalCombinations is NULL. Check evoking params");
            throw new CustomUnexpectedException(ExceptionMessages.TOURNAMENT_SERIES_GENERATION_ERROR);
        }
        TournamentRound tournamentRound = TournamentRound.builder()
                .name("Round #" + roundNumber)
                .roundNumber(roundNumber)
                .status(TournamentStatusType.CREATED)
                .type(TournamentRoundType.DEFAULT)
                .build();

        List<TournamentSeries> tournamentSeriesList = IntStream.range(0, rivalCombinations.size()).parallel()
                .mapToObj(index -> this.generateSeries(tournamentRound,
                        getParentSeries(index, parentTournamentSeriesList),
                        rivalCombinations.get(index), matchCountPerSeries)
                )
                .collect(Collectors.toList());

        tournamentRound.setSeriesList(tournamentSeriesList);
        return tournamentRound;
    }

    /**
     * Returns generated series for specified rivalCombinations, matchCount, seriesPosition
     */
    private TournamentSeries generateSeries(TournamentRound tournamentRound, Set<TournamentSeries> parentSeriesList,
                                            Set<TournamentTeamProposal> rivalCombinations, int matchCount) {
        if (isNull(rivalCombinations)) {
            log.error("!> round generation with TournamentSingleEliminationGeneratorImpl caused error. RivalCombinations is NULL. Check evoking params");
            throw new CustomUnexpectedException(ExceptionMessages.TOURNAMENT_SERIES_GENERATION_ERROR);
        }
        // For Single elimination all series with type 'UPPER_BRACKET'
        TournamentSeries tournamentSeries = TournamentSeries.builder()
                .name(String.format("Series '%s' for %s", StringUtil.generateRandomName(), tournamentRound.getName()))
                .status(TournamentStatusType.CREATED)
                .type(TournamentSeriesBracketType.UPPER_BRACKET)
                .tournamentRound(tournamentRound)
                .parentSeriesList(parentSeriesList)
                .build();

        Set<TournamentSeriesRival> rivalList = rivalCombinations.parallelStream().map(rival ->
                TournamentSeriesRival.builder()
                        .tournamentSeries(tournamentSeries)
                        .status(TournamentMatchRivalParticipantStatusType.ACTIVE)
                        .parentTournamentSeries(parentSeriesList.iterator().next())
                        .teamProposal(rivalCombinations.iterator().next())
                        .build()
        ).collect(Collectors.toSet());
        tournamentSeries.setRivalList(rivalList);

        List<TournamentMatch> tournamentMatchList = Collections.nCopies(matchCount, null).parallelStream()
                .map(i -> this.generateMatch(tournamentSeries, rivalCombinations))
                .collect(Collectors.toList());

        tournamentSeries.setMatchList(tournamentMatchList);
        return tournamentSeries;
    }

    /**
     * Collect two series sequentially from list of series to form "parent" of new series
     */
    private Set<TournamentSeries> getParentSeries(int index, List<TournamentSeries> parentTournamentSeriesList) {
        if (isNull(parentTournamentSeriesList) || parentTournamentSeriesList.isEmpty()) {
            return null;
        }
        int parentSeriesIndex = index * 2;
        // from range [parentSeriesIndex, parentSeriesIndex + 2) means [parentSeriesIndex, parentSeriesIndex + 1]
        return new HashSet<>(parentTournamentSeriesList.subList(parentSeriesIndex, parentSeriesIndex + 2));
    }

    /**
     * Returns new match from specified series and rivalCombination
     */
    private TournamentMatch generateMatch(TournamentSeries tournamentSeries,
                                          Set<TournamentTeamProposal> rivalCombination) {
        TournamentMatch tournamentMatch = TournamentMatch.builder()
                .tournamentSeries(tournamentSeries)
                .name(String.format("Match '%s' for %s", StringUtil.generateRandomName(), tournamentSeries.getName()))
                .status(TournamentStatusType.CREATED)

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
     * for default rival participant composition we add  participants of Tournament Team Proposal with State
     */
    private TournamentMatchRival generateMatchRival(TournamentMatch tournamentMatch, TournamentTeamProposal teamProposal) {
        TournamentMatchRival matchRival = TournamentMatchRival.builder()
                .status(TournamentMatchRivalParticipantStatusType.ACTIVE)
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
    private int calculateSeriesCountForRound(int teamProposalListSize, Integer matchRivalsCount) {
        // team count divide by count of rival per match
        return (teamProposalListSize / matchRivalsCount);
    }

    /**
     * Returns collection of random generated rival teams (TournamentTeamProposal) for further processing
     */
    private List<Set<TournamentTeamProposal>> composeRivalCombinationsFromTeamProposal(Integer rivalCombinationCount,
                                                                                       Integer rivalCountPerMatch,
                                                                                       List<TournamentTeamProposal> teamProposalList) {
        if (isNull(rivalCombinationCount) || isNull(rivalCountPerMatch) || isNull(teamProposalList)) {
            log.error("!> requesting generate rival combination for NULL rivalCombinationCount {} " +
                            "or NULL matchRivalCount {} or NULL teamProposalList {}. Check evoking clients",
                    rivalCombinationCount, rivalCountPerMatch, teamProposalList);
            return null;
        }

        List<Set<TournamentTeamProposal>> rivalCombinationList = new ArrayList<>(rivalCombinationCount);
        for (int i = 0; i < rivalCombinationCount; i++) {
            Set<TournamentTeamProposal> teamProposalSet = new HashSet<>(rivalCountPerMatch);
            // collect one combination of rivals with specified matchRivalCount
            for (int j = 0; j < rivalCountPerMatch; j++) {
                if (teamProposalList.isEmpty()) {
                    break;
                }
                int randomTeam = randomGenerator(teamProposalList.size());
                TournamentTeamProposal teamProposal = teamProposalList.get(randomTeam);
                //removing chosen team proposal from list
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
