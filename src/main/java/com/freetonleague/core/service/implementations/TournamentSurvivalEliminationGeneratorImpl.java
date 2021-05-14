package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.enums.*;
import com.freetonleague.core.domain.model.*;
import com.freetonleague.core.exception.CustomUnexpectedException;
import com.freetonleague.core.exception.ExceptionMessages;
import com.freetonleague.core.service.TournamentGenerator;
import com.freetonleague.core.service.TournamentProposalService;
import com.freetonleague.core.service.TournamentService;
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
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Slf4j
@RequiredArgsConstructor
@Component("survivalEliminationGenerator")
public class TournamentSurvivalEliminationGeneratorImpl implements TournamentGenerator {

    private final TournamentProposalService tournamentProposalService;

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
     * Tournament should be with empty round list.
     */
    @Override
    public List<TournamentRound> generateRoundsForTournament(Tournament tournament) {
        log.debug("^ trying to generate rounds for tournament.id {} with Survival Elimination algorithm", tournament.getId());
        if (!TournamentStatusType.activeStatusList.contains(tournament.getStatus())) {
            log.error("!> requesting generate survival tournament rounds for non-active tournament. Check evoking clients");
            return null;
        }
        if (!tournament.getTournamentRoundList().isEmpty()) {
            log.error("!> requesting generate survival tournament rounds for tournament with non-empty Round list. Check evoking clients");
            return null;
        }
        //define vars for algorithm
        GameDisciplineSettings gameDisciplineSettings = tournament.getGameDisciplineSettings();
        TournamentSettings tournamentSettings = tournament.getTournamentSettings();
        List<TournamentTeamProposal> teamProposalList = tournamentProposalService.getActiveTeamProposalListByTournament(tournament);
        Integer matchRivalCount = gameDisciplineSettings.getMatchRivalCount();
        Integer matchCountPerSeries = tournamentSettings.getMatchCountPerSeries();
        int teamProposalListSize = teamProposalList.size();

        if (!this.isValidTeamCountForSurvival(teamProposalListSize, matchRivalCount)) {
            log.error("!> requesting generate survival tournament rounds for tournament with non-acceptable '{}' count of rivals" +
                    " (approved team proposals). Check evoking clients", teamProposalListSize);
            return null;
        }

        int seriesCountInRound;
        int currentRoundNumber = 1;
        List<List<TournamentTeamProposal>> rivalCombinations;
        List<TournamentSeries> parentTournamentSeriesList = new ArrayList<>();
        List<TournamentRound> newTournamentRoundList = new ArrayList<>();

        //generate round, series and matches
        //for round #2+ series(and inner matches) will be only prototypes, they will be filled with rivals as soon as first round finish
        do {
            // count round based upon discipline/tournament settings and teamProposalList size
            seriesCountInRound = this.calculateSeriesCountForRound(currentRoundNumber, teamProposalListSize, matchRivalCount);
            // get rival combinations or empty list for 2+ round
            rivalCombinations = this.composeRivalCombinationsFromTeamProposals(seriesCountInRound,
                    matchRivalCount, teamProposalList);
            TournamentRound newRound = this.generateRound(rivalCombinations, parentTournamentSeriesList,
                    currentRoundNumber, matchCountPerSeries);
            newRound.setTournament(tournament);
            newTournamentRoundList.add(newRound);
            parentTournamentSeriesList = newRound.getSeriesList();
            currentRoundNumber++;

        } while (seriesCountInRound > 1);

        return newTournamentRoundList;
    }

    /**
     * Compose series, matches and rivals for tournament round. Look to parents for series in specified tournamentRound and compose rivals.
     * Tournament Round should open.
     */
    @Override
    public TournamentRound composeNextRoundForTournament(TournamentRound tournamentRound) {
        log.debug("^ trying to compose matches for new round for tournamentRound.id {} with Survival Elimination algorithm",
                tournamentRound.getId());
        if (!TournamentStatusType.activeStatusList.contains(tournamentRound.getStatus())) {
            log.error("!> requesting composeNewRoundForTournament for not active survival tournament round status. Check evoking clients");
            return null;
        }
        if (tournamentRound.getRoundNumber() == 1) {
            log.error("!> requesting composeNewRoundForTournament not initiated survival tournament with no finished round. Check evoking clients");
            return null;
        }
        if (tournamentRound.getSeriesList().isEmpty()) {
            log.error("!> requesting composeNewRoundForTournament for survival round with non-empty Series list. Check evoking clients");
            return null;
        }

        GameDisciplineSettings gameDisciplineSettings = tournamentRound.getTournament().getGameDisciplineSettings();
        int matchRivalWinners = gameDisciplineSettings.getMatchRivalWinnersCount();
        int matchRivalCount = gameDisciplineSettings.getMatchRivalCount();
        int matchCountPerSeries = tournamentRound.getTournament().getTournamentSettings().getMatchCountPerSeries();

        List<TournamentSeries> roundSeriesList = tournamentRound.getSeriesList();
        List<TournamentTeamProposal> roundRivalList = roundSeriesList.parallelStream()
                .map(TournamentSeries::getParentSeriesList)
                .flatMap(Collection::stream)
                .map(s -> this.filterSeriesRivalWinnersFromSeries(s, matchRivalWinners))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        if (!this.isValidTeamCountForSurvival(roundRivalList.size(), matchRivalCount)) {
            log.error("!> requesting composeNewRoundForTournament for survival round with not valid team rivals count " +
                    "(winners of prev round) '{}'. Check evoking clients", roundRivalList.size());
            return null;
        }

        int seriesCountInRound = roundSeriesList.size();

        // compose updates series list (for round) with filled rivals
        List<List<TournamentTeamProposal>> rivalCombinations = this.composeRivalCombinationsFromTeamProposals(seriesCountInRound,
                matchRivalCount, roundRivalList);

        if (isEmpty(rivalCombinations)) {
            log.error("!> round compose with TournamentSingleEliminationGeneratorImpl caused error. " +
                    "RivalCombinations is NULL. Check evoking params");
            throw new CustomUnexpectedException(ExceptionMessages.TOURNAMENT_SERIES_GENERATION_ERROR);
        }

//        List<TournamentSeries> tournamentSeriesList = IntStream.range(0, seriesCountInRound).parallel()
        List<TournamentSeries> updatedTournamentSeriesList = IntStream.range(0, rivalCombinations.size()).parallel()
                .mapToObj(index -> this.composeAndFillRivalsOfTournamentSurvivalSeries(roundSeriesList.get(index),
                        rivalCombinations.get(index), matchCountPerSeries)
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (isEmpty(updatedTournamentSeriesList)
                || updatedTournamentSeriesList.size() != tournamentRound.getSeriesList().size()) {
            log.error("!> requesting composeNewRoundForTournament for errors in series list for round.id {}. " +
                    "Check stack trace and evoking clients", tournamentRound.getId());
            throw new CustomUnexpectedException(ExceptionMessages.TOURNAMENT_SERIES_GENERATION_ERROR);
        }
        tournamentRound.setSeriesList(updatedTournamentSeriesList);
        return tournamentRound;
    }

    /**
     * Returns filtered Series Rival by Won place in Series and verification count
     */
    private List<TournamentTeamProposal> filterSeriesRivalWinnersFromSeries(TournamentSeries tournamentSeries, int matchRivalWinners) {
        List<TournamentWinnerPlaceType> winnerPlaceTypeToPassedList = TournamentWinnerPlaceType
                .getPlaceListWithLimit(matchRivalWinners);
        if (isEmpty(winnerPlaceTypeToPassedList)) {
            log.error("!> requesting composeNewRoundForTournament for survival round for non-existed " +
                    "winnerPlaceTypeToPassed list. Check evoking clients");
            return null;
        }
        List<TournamentTeamProposal> seriesRivalWinners = tournamentSeries.getSeriesRivalList().parallelStream()
                .filter(rival -> winnerPlaceTypeToPassedList.contains(rival.getWonPlaceInSeries()))
                .map(TournamentSeriesRival::getTeamProposal)
                .collect(Collectors.toList());
        // check if filtered size is match for count of expected matchRivalWinners
        if (seriesRivalWinners.size() != matchRivalWinners) {
            log.error("!!> requesting composeNewRoundForTournament for errors in series rival winners for series.id {}. " +
                            "Series rival winner size is {}, but expected {}. Check stack trace and evoking clients",
                    tournamentSeries.getId(), seriesRivalWinners.size(), matchRivalWinners);
            throw new CustomUnexpectedException(ExceptionMessages.TOURNAMENT_SERIES_GENERATION_ERROR);
        }
        return seriesRivalWinners;
    }


    /**
     * Generate new match (OMT) for series.
     */
    @Override
    public TournamentMatch generateOmtForSeries(TournamentSeries tournamentSeries) {
        List<TournamentMatch> tournamentMatchList = tournamentSeries.getMatchList();
        return this.generateMatch(tournamentMatchList.size() + 1,
                tournamentSeries, tournamentSeries.getTeamProposalList());
    }

    /**
     * Update series and fill rivals in series and matches
     * Only for already initiated Tournament wit prototypes of Series (for composeNextRoundForTournament)
     */
    private TournamentSeries composeAndFillRivalsOfTournamentSurvivalSeries(TournamentSeries currentSeries,
                                                                            List<TournamentTeamProposal> rivalCombinations,
                                                                            int matchCountPerSeries) {
        if (isNotEmpty(currentSeries.getSeriesRivalList())) {
            log.error("!> requesting composeAndFillRivalsOfTournamentSurvivalSeries in composeNewRoundForTournament for not empty " +
                    "list of series rival for series {}. Check evoking clients.", currentSeries);
            return null;
        }
        List<TournamentSeries> parentSeriesList = currentSeries.getParentSeriesList();

        List<TournamentSeriesRival> rivalList = rivalCombinations.parallelStream()
                .map(rival -> this.generateSeriesRival(currentSeries,
                        parentSeriesList.get(randomGenerator(parentSeriesList.size())), rival))
                .collect(Collectors.toList());

        currentSeries.setSeriesRivalList(rivalList);

        List<TournamentMatch> tournamentMatchList = IntStream.range(1, matchCountPerSeries + 1).parallel()
                .mapToObj(index -> this.generateMatch(index, currentSeries, rivalCombinations))
                .collect(Collectors.toList());

        currentSeries.setMatchList(tournamentMatchList);
        return currentSeries;
    }

    /**
     * Returns generated series for specified rivalCombinations, matchCount, seriesPosition
     */
    private TournamentRound generateRound(List<List<TournamentTeamProposal>> rivalCombinations,
                                          List<TournamentSeries> parentTournamentSeriesList,
                                          Integer roundNumber, int matchCountPerSeries) {
        if (isEmpty(rivalCombinations)) {
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
    private TournamentSeries generateSeries(TournamentRound tournamentRound, List<TournamentSeries> parentSeriesList,
                                            List<TournamentTeamProposal> rivalCombinations, int matchCount) {
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

        List<TournamentSeriesRival> rivalList = rivalCombinations.parallelStream().map(rival ->
                this.generateSeriesRival(tournamentSeries,
                        nonNull(parentSeriesList) ? parentSeriesList.iterator().next() : null,
                        rival)
        ).collect(Collectors.toList());
        tournamentSeries.setSeriesRivalList(rivalList);

        List<TournamentMatch> tournamentMatchList = IntStream.range(1, matchCount + 1).parallel()
                .mapToObj(index -> this.generateMatch(index, tournamentSeries, rivalCombinations))
                .collect(Collectors.toList());

        tournamentSeries.setMatchList(tournamentMatchList);
        return tournamentSeries;
    }

    /**
     * Collect two series sequentially from list of series to form "parent" of new series
     */
    private List<TournamentSeries> getParentSeries(int index, List<TournamentSeries> parentTournamentSeriesList) {
        if (isNull(parentTournamentSeriesList) || parentTournamentSeriesList.isEmpty()) {
            return null;
        }
        int parentSeriesIndex = index * 2;
        // from range [parentSeriesIndex, parentSeriesIndex + 2) means [parentSeriesIndex, parentSeriesIndex + 1]
        return parentTournamentSeriesList.subList(parentSeriesIndex, parentSeriesIndex + 2);
    }

    /**
     * Create TournamentSeriesRival for Series
     */
    private TournamentSeriesRival generateSeriesRival(TournamentSeries existedSeries, TournamentSeries parentSeries,
                                                      TournamentTeamProposal tournamentTeamProposal) {
        return TournamentSeriesRival.builder()
                .tournamentSeries(existedSeries)
                .status(TournamentMatchRivalParticipantStatusType.ACTIVE)
                .parentTournamentSeries(parentSeries)
                .teamProposal(tournamentTeamProposal)
                .build();
    }

    /**
     * Returns new match from specified series and rivalCombination
     */
    private TournamentMatch generateMatch(int matchIndex, TournamentSeries tournamentSeries,
                                          List<TournamentTeamProposal> rivalCombination) {
        TournamentMatch tournamentMatch = TournamentMatch.builder()
                .tournamentSeries(tournamentSeries)
                .matchNumberInSeries(matchIndex)
                .name(String.format("Match '%s' for %s", StringUtil.generateRandomName(), tournamentSeries.getName()))
                .status(TournamentStatusType.CREATED)

                .build();
        List<TournamentMatchRival> tournamentMatchRivalList = new ArrayList<>();
        // create and collect match rivals from specified rival combinations
        if (!rivalCombination.isEmpty()) {
            // get rival combination for one match and create rivals
            rivalCombination.parallelStream()
                    .forEach(teamProposal -> {
                        // add new match rival to list
                        tournamentMatchRivalList.add(this.generateMatchRival(tournamentMatch, teamProposal));
                    });
        }
        tournamentMatch.setMatchRivalList(tournamentMatchRivalList);
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
    private int calculateSeriesCountForRound(int roundIndex, int teamProposalListSize, Integer matchRivalsCount) {
        // team count divide by current round index and by count of rival per match
        return teamProposalListSize / (matchRivalsCount * (int) Math.pow(2, roundIndex - 1));
    }

    /**
     * Returns collection of random generated rival teams (TournamentTeamProposal) for further processing
     */
    private List<List<TournamentTeamProposal>> composeRivalCombinationsFromTeamProposals(Integer rivalCombinationCount,
                                                                                         Integer rivalCountPerMatch,
                                                                                         List<TournamentTeamProposal> teamProposalList) {
        if (isNull(rivalCombinationCount) || isNull(rivalCountPerMatch) || isNull(teamProposalList)) {
            log.error("!> requesting generate rival combination for NULL rivalCombinationCount {} " +
                            "or NULL matchRivalCount {} or NULL teamProposalList {}. Check evoking clients",
                    rivalCombinationCount, rivalCountPerMatch, teamProposalList);
            return null;
        }

        List<List<TournamentTeamProposal>> rivalCombinationList = new ArrayList<>(rivalCombinationCount);
        for (int i = 0; i < rivalCombinationCount; i++) {
            List<TournamentTeamProposal> teamProposalSet = new ArrayList<>(rivalCountPerMatch);
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
    private boolean isValidTeamCountForSurvival(int teamCount, int countRivalPerMatch) {
        if (teamCount == 0 || teamCount < countRivalPerMatch) {
            return false;
        }
        int value = teamCount / countRivalPerMatch;
        while (value % 2 == 0) {
            value /= 2;
        }
        return value == 1;
    }
}
