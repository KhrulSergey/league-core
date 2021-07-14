package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.enums.*;
import com.freetonleague.core.domain.model.*;
import com.freetonleague.core.exception.CustomUnexpectedException;
import com.freetonleague.core.exception.config.ExceptionMessages;
import com.freetonleague.core.service.TournamentGenerator;
import com.freetonleague.core.service.TournamentProposalService;
import com.freetonleague.core.service.TournamentRoundService;
import com.freetonleague.core.service.TournamentSeriesService;
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
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Slf4j
@RequiredArgsConstructor
@Component("singleEliminationGenerator")
public class TournamentSingleEliminationGeneratorImpl implements TournamentGenerator {

    private final TournamentProposalService tournamentProposalService;

    @Lazy
    @Autowired
    private TournamentRoundService tournamentRoundService;

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
    public List<TournamentRound> initiateTournamentBracketsWithRounds(Tournament tournament) {
        log.debug("^ trying to generate rounds for tournament.id '{}' with Single Elimination algorithm", tournament.getId());
        if (!TournamentStatusType.activeStatusList.contains(tournament.getStatus())) {
            log.error("!> requesting generate tournament round for non-active tournament. Check evoking clients");
            return null;
        }
        if (!tournament.getTournamentRoundList().isEmpty()) {
            log.error("!> requesting generate tournament round for tournament with non-empty Round list. Check evoking clients");
            return null;
        }
        //define vars for algorithm
        GameDisciplineSettings gameDisciplineSettings = tournament.getGameDisciplineSettings();
        TournamentSettings tournamentSettings = tournament.getTournamentSettings();
        List<TournamentTeamProposal> teamProposalList = tournamentProposalService.getApprovedTeamProposalListByTournament(tournament);
        int matchRivalCount = gameDisciplineSettings.getMatchRivalCount();
        Integer matchCountPerSeries = tournamentSettings.getMatchCountPerSeries();
        int teamProposalListSize = teamProposalList.size();

        if (!this.isPowerOfN(teamProposalListSize, matchRivalCount)) {
            log.warn("!> requesting generate tournament round for tournament with not complete '{}' count of rivals" +
                    " (approved team proposals). Continue to generate round with incomplete series", teamProposalListSize);
        }

        int seriesCount;
        int currentRoundNumber = 1;
        List<List<TournamentTeamProposal>> rivalCombinations;
        List<TournamentSeries> parentTournamentSeriesList = new ArrayList<>();
        List<TournamentRound> newTournamentRoundList = new ArrayList<>();
        tournament.setTournamentRoundList(newTournamentRoundList);

        //generate round, series and matches
        //for round #2+ series(and inner matches) will be only prototypes, they will be filled with rivals as soon as first round finish
        do {
            // count round based upon discipline/tournament settings and teamProposalList size
            seriesCount = this.calculateSeriesCountForRound(teamProposalListSize, matchRivalCount);
            // get rival combinations or empty list for 2+ round
            rivalCombinations = this.composeRoundRivalCombinationsFromTeamProposal(seriesCount,
                    matchRivalCount, teamProposalList);
            TournamentRound newRound = this.generateRound(tournament, rivalCombinations, matchRivalCount, parentTournamentSeriesList,
                    currentRoundNumber, matchCountPerSeries);
            newTournamentRoundList.add(newRound);
            parentTournamentSeriesList = newRound.getSeriesList();
            teamProposalListSize = newRound.getSeriesList().size();
            currentRoundNumber++;
        } while (seriesCount > 1);

        return newTournamentRoundList;
    }

    /**
     * Compose additional tournament settings based upon tournament template.
     * e.g. Generate default round settings from embedded tournament settings
     */
    @Override
    public TournamentSettings composeAdditionalTournamentSettings(Tournament tournament) {
        //no need to compose additional settings
        return tournament.getTournamentSettings();
    }

    /**
     * Compose series, matches and rivals for tournament round. Look to parents for series in specified tournamentRound and compose rivals.
     * Tournament Round should open.
     */
    @Override
    public TournamentRound composeNextRoundForTournament(Tournament tournament) {
        log.debug("^ trying to compose matches for new opened round for tournament.id '{}' with Single Elimination algorithm",
                tournament.getId());

        TournamentRound tournamentRound = tournamentRoundService.getNextOpenRoundForTournament(tournament);
        if (isNull(tournamentRound)) {
            log.error("!> requesting composeNewRoundForTournament for tournament with no existed open round for compose series. Check evoking clients");
            return null;
        }

        if (!TournamentStatusType.activeStatusList.contains(tournamentRound.getStatus())) {
            log.error("!> requesting composeNewRoundForTournament for not active tournament round status. Check evoking clients");
            return null;
        }
        if (tournamentRound.getRoundNumber() == 1) {
            log.error("!> requesting composeNewRoundForTournament not initiated tournament with no finished round. Check evoking clients");
            return null;
        }
        if (tournamentRound.getSeriesList().isEmpty()) {
            log.error("!> requesting composeNewRoundForTournament for round with empty Series list - " +
                    "means some error was with generateRoundsForTournament or data in DB. Check evoking clients");
            return null;
        }
        GameDisciplineSettings gameDisciplineSettings = tournament.getGameDisciplineSettings();
        int matchCountPerSeries = tournament.getTournamentSettings().getMatchCountPerSeries();
        int matchRivalCount = gameDisciplineSettings.getMatchRivalCount();
        // compose updates series list with filled rivals
        List<TournamentSeries> updatedTournamentSeriesList = tournamentRound.getSeriesList().parallelStream()
                .map(series -> this.composeAndFillRivalsOfTournamentSeries(series, matchRivalCount, matchCountPerSeries))
                .filter(Objects::nonNull).collect(Collectors.toList());
        if (isEmpty(updatedTournamentSeriesList)
                || updatedTournamentSeriesList.size() != tournamentRound.getSeriesList().size()) {
            log.error("!> requesting composeNewRoundForTournament for errors in series list for round.id '{}'. " +
                    "Check stack trace and evoking clients", tournamentRound.getId());
            throw new CustomUnexpectedException(ExceptionMessages.TOURNAMENT_SERIES_GENERATION_ERROR);
        }

        tournamentRound.setSeriesList(updatedTournamentSeriesList);
        return tournamentRound;
    }

    /**
     * Compose rival for next series (child) by get winner of parent-specified series. If next series is full of rivals then compose matches.
     * Look to specified series - find it's winner X. Look to specified series - find it's child series Y. Compose rival od series Y as winner X.
     */
    @Override
    public TournamentSeries composeRivalForChildTournamentSeries(TournamentSeries prevTournamentSeries) {
        log.debug("^ initiated composeRivalForChildTournamentSeries for prevTournamentSeries.id '{}'", prevTournamentSeries.getId());
        TournamentSeries generatedSeries = prevTournamentSeries.getChildSeries();
        if (isNull(generatedSeries)) {
            log.warn("^ we have no child for prevTournamentSeries.id '{}' in composeRivalForChildTournamentSeries. Nothing to compose", prevTournamentSeries.getId());
            return null;
        }
        Tournament tournament = prevTournamentSeries.getTournamentRound().getTournament();
        GameDisciplineSettings gameDisciplineSettings = tournament.getGameDisciplineSettings();
        int matchCountPerSeries = tournament.getTournamentSettings().getMatchCountPerSeries();
        int matchRivalCount = gameDisciplineSettings.getMatchRivalCount();

        List<TournamentSeriesRival> generatedSeriesRivalList = generatedSeries.getSeriesRivalList();
        log.debug("^ try to composeRivalForChildTournamentSeries for generatedSeries.id '{}'", generatedSeries.getId());
        if (isTrue(generatedSeries.getIsIncomplete())) { // we have series with already one 'blank' rival
            log.debug("^ we have already incomplete series.id '{}'. Define winners from prev series.id '{}'", generatedSeries.getId(), prevTournamentSeries.getId());
            if (prevTournamentSeries.getHasNoWinner()) {
                log.debug("^ we have no winner in new generated series.id '{}', because prev series.id '{}' hasNoWinner." +
                        "We got all previous series end with hasNoWinner signs.", generatedSeries.getId(), prevTournamentSeries.getId());
                generatedSeries.setHasNoWinner(true);
                generatedSeries.setStatus(TournamentStatusType.FINISHED);
            } else {
                // we have only one winner (! for 2 rivals in series)
                log.debug("^ we have only one winner (! for 2 rivals in series)series.id '{}'." +
                        " Means all previous series end with hasNoWinner signs. So we get winner from prevSeries and finish the series", generatedSeries.getId());
                TournamentSeriesRival seriesRival = this.generateSeriesRival(generatedSeries, prevTournamentSeries,
                        prevTournamentSeries.getTeamProposalWinner());
                if (isNull(seriesRival)) {
                    log.error("!> requesting composeRivalForChildTournamentSeries for errors in series winner for tournamentSeries.id '{}'. " +
                            "Check stack trace and evoking clients", prevTournamentSeries.getId());
                    throw new CustomUnexpectedException(ExceptionMessages.TOURNAMENT_SERIES_GENERATION_ERROR);
                }
                generatedSeries.setStatus(TournamentStatusType.FINISHED);
                seriesRival.setWonPlaceInSeries(TournamentWinnerPlaceType.FIRST);
                generatedSeries.setSeriesWinner(seriesRival);
            }
        } else {
            log.debug("^ we have 'blank' series.id '{}' or it have only one filled rival.", generatedSeries.getId());
            // check if prev series end with hasNoWinnerFlag
            if (isTrue(prevTournamentSeries.getHasNoWinner())) {
                log.debug("^ prevSeries.id '{}' ends with hasNoWinner flag. So the series.id '{}' will be incomplete",
                        prevTournamentSeries.getId(), generatedSeries.getId());
                generatedSeries.setIsIncomplete(true);
                if (generatedSeriesRivalList.size() == matchRivalCount - 1) {
                    log.debug("^ we already had some rivals in the series.id {} and prevSeries.id '{}' ends with hasNoWinner flag." +
                                    " So this rival (for 2 rivals per series) id '{}' is the winner'",
                            generatedSeries.getId(), prevTournamentSeries.getId(), generatedSeriesRivalList.get(0).getId());
                    TournamentSeriesRival seriesRivalWinner = generatedSeriesRivalList.get(0);
                    generatedSeries.setStatus(TournamentStatusType.FINISHED);
                    seriesRivalWinner.setWonPlaceInSeries(TournamentWinnerPlaceType.FIRST);
                    generatedSeries.setSeriesWinner(seriesRivalWinner);
                }
            } else {
                TournamentTeamProposal tournamentTeamProposal = prevTournamentSeries.getTeamProposalWinner();
                if (isNull(tournamentTeamProposal)) {
                    log.error("!> requesting composeRivalForChildTournamentSeries for errors in series winner for tournamentSeries.id '{}'. " +
                            "Check stack trace and evoking clients", prevTournamentSeries.getId());
                    throw new CustomUnexpectedException(ExceptionMessages.TOURNAMENT_SERIES_GENERATION_ERROR);
                }
                log.debug("^ we have some winner of prevSeries.id '{}'. So add this teamProposal.id '{}' as rival for the series.id '{}'",
                        prevTournamentSeries.getId(), tournamentTeamProposal.getId(), generatedSeries.getId());
                TournamentSeriesRival seriesRival = this.generateSeriesRival(generatedSeries, prevTournamentSeries,
                        prevTournamentSeries.getTeamProposalWinner());
                if (isNull(seriesRival)) {
                    log.error("!> requesting composeRivalForChildTournamentSeries for errors with generateSeriesRival " +
                                    "from series winner for tournamentSeries.id '{}'. Check stack trace and evoking clients",
                            prevTournamentSeries.getId());
                    throw new CustomUnexpectedException(ExceptionMessages.TOURNAMENT_SERIES_GENERATION_ERROR);
                }
                generatedSeriesRivalList.add(seriesRival);
                if (generatedSeriesRivalList.size() == matchRivalCount) {
                    log.debug("^ we have suitable kit of '{}' rivals for the series.id '{}'. Generate match list",
                            matchRivalCount, generatedSeries.getId());
                    generatedSeries.setMatchList(this.generateMatchesWithRivals(generatedSeries,
                            generatedSeriesRivalList, matchCountPerSeries));
                }
            }
        }
        return generatedSeries;
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
    private TournamentSeries composeAndFillRivalsOfTournamentSeries(TournamentSeries currentSeries,
                                                                    int matchRivalCount, int matchCountPerSeries) {
        if (isNotEmpty(currentSeries.getSeriesRivalList())) {
            log.error("!> requesting composeAndFillRivalsOfTournamentSeries in composeNewRoundForTournament for not empty " +
                    "list of series rival for series '{}'. Check evoking clients.", currentSeries);
            return null;
        }

        // collect and build series rival for current series
        List<TournamentSeriesRival> rivalList = currentSeries.getParentSeriesList().stream()
                .map(parentSeries -> this.generateSeriesRival(currentSeries, parentSeries, parentSeries.getTeamProposalWinner()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        // update series rivals in current series
        currentSeries.setSeriesRivalList(rivalList);

        boolean hasNoWinner = isEmpty(rivalList);
        boolean isIncomplete = rivalList.size() < matchRivalCount;
        currentSeries.setHasNoWinner(hasNoWinner);
        currentSeries.setIsIncomplete(isIncomplete);
        if (isIncomplete) {
            currentSeries.setStatus(TournamentStatusType.FINISHED);
            if (!hasNoWinner) {
                //take first rival from series and set him as winner (will not work for 3+ rivals per series)
                TournamentSeriesRival winner = rivalList.get(0);
                winner.setWonPlaceInSeries(TournamentWinnerPlaceType.FIRST);
                currentSeries.setSeriesWinner(winner);
            }
        } else {
            currentSeries.setMatchList(this.generateMatchesWithRivals(currentSeries, rivalList, matchCountPerSeries));
        }
        return currentSeries;
    }

    /**
     * Returns generated series for specified rivalCombinations, matchCount, seriesPosition
     */
    private TournamentRound generateRound(Tournament tournament, List<List<TournamentTeamProposal>> rivalCombinations,
                                          int matchRivalCount,
                                          List<TournamentSeries> parentTournamentSeriesList,
                                          Integer roundNumber, int matchCountPerSeries) {
        if (isNull(rivalCombinations)) {
            log.error("!> round generation with TournamentSingleEliminationGeneratorImpl caused error. RivalCombinations is NULL. Check evoking params");
            throw new CustomUnexpectedException(ExceptionMessages.TOURNAMENT_SERIES_GENERATION_ERROR);
        }
        //define if current round consist of only 1 series, then it should be the last round in tournament
        boolean isLastRound = rivalCombinations.size() == 1;
        TournamentRound tournamentRound = TournamentRound.builder()
                .name("Round #" + roundNumber)
                .roundNumber(roundNumber)
                .tournament(tournament)
                .status(TournamentStatusType.CREATED)
                .type(TournamentRoundType.DEFAULT)
                .isLast(isLastRound)
                .build();

        List<TournamentSeries> tournamentSeriesList = IntStream.range(0, rivalCombinations.size()).parallel()
                .mapToObj(index -> this.generateSeries(tournamentRound,
                        this.getParentSeries(index, parentTournamentSeriesList),
                        rivalCombinations.get(index), matchRivalCount, matchCountPerSeries)
                )
                .collect(Collectors.toList());

        tournamentRound.setSeriesList(tournamentSeriesList);
        return tournamentRound;
    }

    /**
     * Returns generated series for specified rivalCombinations, matchCount, seriesPosition
     */
    private TournamentSeries generateSeries(TournamentRound tournamentRound, List<TournamentSeries> parentSeriesList,
                                            List<TournamentTeamProposal> rivalCombinations, int matchRivalCount, int matchCount) {
        if (isNull(rivalCombinations)) {
            log.error("!> round generation with TournamentSingleEliminationGeneratorImpl caused error. RivalCombinations is NULL. Check evoking params");
            throw new CustomUnexpectedException(ExceptionMessages.TOURNAMENT_SERIES_GENERATION_ERROR);
        }
        //if we are going to generate 'prototype' of current series but if sequentialSeriesEnabled,
        // then we check if there is any incomplete parent series
        // if so -> we collect the winners of them to compose rivalCombinations for current series
        if (isEmpty(rivalCombinations)
                && tournamentRound.getTournament().getTournamentSettings().getIsSequentialSeriesEnabled()
                && isNotEmpty(parentSeriesList)) {
            rivalCombinations = parentSeriesList.parallelStream().filter(s -> isTrue(s.getIsIncomplete()))
                    .map(TournamentSeries::getSeriesWinner).map(TournamentSeriesRival::getTeamProposal)
                    .collect(Collectors.toList());
        }
        //series is incomplete if current rivalCombinations size less than required matchRivalCount.
        //if rivalCombinations is Empty than we generate 'prototype' of series -> isIncomplete=null
        Boolean isIncomplete = isNotEmpty(rivalCombinations) ?
                rivalCombinations.size() < matchRivalCount
                : null;
        // For Single elimination all series with type 'UPPER_BRACKET'
        TournamentSeries tournamentSeries = TournamentSeries.builder()
                .name(String.format("Series '%s' for %s", StringUtil.generateRandomName(), tournamentRound.getName()))
                .status(TournamentStatusType.CREATED)
                .type(TournamentSeriesBracketType.UPPER_BRACKET)
                .isIncomplete(isIncomplete)
                .tournamentRound(tournamentRound)
                .parentSeriesList(parentSeriesList)
                .build();

        List<TournamentSeriesRival> rivalList = rivalCombinations.parallelStream().map(rival ->
                this.generateSeriesRival(tournamentSeries,
                        nonNull(parentSeriesList) ? parentSeriesList.iterator().next() : null,
                        rival)
        ).collect(Collectors.toList());
        tournamentSeries.setSeriesRivalList(rivalList);

        //if isIncomplete is NULL than we generate 'prototype' of series
        if (nonNull(isIncomplete)) {
            if (isIncomplete) {
                tournamentSeries.setStatus(TournamentStatusType.FINISHED);
                tournamentSeries.setSeriesWinner(rivalList.get(0));
            } else {
                List<TournamentTeamProposal> finalRivalCombinations = rivalCombinations;
                List<TournamentMatch> tournamentMatchList = IntStream.range(1, matchCount + 1).parallel()
                        .mapToObj(index -> this.generateMatch(index, tournamentSeries, finalRivalCombinations))
                        .collect(Collectors.toList());
                tournamentSeries.setMatchList(tournamentMatchList);
            }
        }
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
    private TournamentSeriesRival generateSeriesRival(TournamentSeries existedSeries, TournamentSeries
            parentSeries,
                                                      TournamentTeamProposal tournamentTeamProposal) {
        if (isNull(tournamentTeamProposal)) {
            log.warn("~ generate series rival for series.id '{}' name '{}' for NULL tournamentTeamProposal. " +
                    "So there we no winner in parentSeries '{}' or we have a error", existedSeries.getId(), existedSeries.getName(), parentSeries);
            return null;
        }
        TournamentSeriesRival tournamentSeriesRival = TournamentSeriesRival.builder()
                .tournamentSeries(existedSeries)
                .status(TournamentMatchRivalParticipantStatusType.ACTIVE)
                .parentTournamentSeries(parentSeries)
                .teamProposal(tournamentTeamProposal)
                .build();
        if (isTrue(existedSeries.getIsIncomplete())) {
            tournamentSeriesRival.setWonPlaceInSeries(TournamentWinnerPlaceType.FIRST);
        }
        return tournamentSeriesRival;
    }

    /**
     * Generate match list for series and fill each match with rivals (from TournamentSeriesRival list above)
     */
    private List<TournamentMatch> generateMatchesWithRivals(TournamentSeries tournamentSeries,
                                                            List<TournamentSeriesRival> seriesRivalList,
                                                            int matchCountPerSeries) {
        List<TournamentTeamProposal> matchRivalCombinations = seriesRivalList.parallelStream()
                .map(TournamentSeriesRival::getTeamProposal).collect(Collectors.toList());
        List<TournamentMatch> tournamentMatchList = IntStream.range(1, matchCountPerSeries + 1).parallel() // +1 due to range (1,x]
                .mapToObj(index -> this.generateMatch(index, tournamentSeries, matchRivalCombinations))
                .collect(Collectors.toList());
        return tournamentMatchList;
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
        if (isNotEmpty(rivalCombination)) {
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
    private TournamentMatchRival generateMatchRival(TournamentMatch tournamentMatch, TournamentTeamProposal
            teamProposal) {
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
     * Returns collection of random generated rival teams (TournamentTeamProposal) for further processing
     */
    private List<List<TournamentTeamProposal>> composeRoundRivalCombinationsFromTeamProposal(Integer
                                                                                                     rivalTotalCombinationCount,
                                                                                             Integer rivalCountPerMatch,
                                                                                             List<TournamentTeamProposal> teamProposalList) {
        if (isNull(rivalTotalCombinationCount) || isNull(rivalCountPerMatch) || isNull(teamProposalList)) {
            log.error("!> requesting generate rival combination for NULL rivalCombinationCount '{}'" +
                            " or NULL matchRivalCount '{}' or NULL teamProposal  List '{}'. Check evoking clients",
                    rivalTotalCombinationCount, rivalCountPerMatch, teamProposalList);
            return null;
        }

        List<List<TournamentTeamProposal>> rivalCombinationList = new LinkedList<>();
        // goes within list of teamProposalList and fill circularly rivalCombinationList for each series:
        // at first cycle fill be one rival, at second cycle fill second rival
        while (!teamProposalList.isEmpty()) {
            // collect one combination of rivals with specified matchRivalCount
            for (int j = 0; j < rivalTotalCombinationCount; j++) {
                if (teamProposalList.isEmpty()) {
                    break;
                }
                List<TournamentTeamProposal> rivalTeamProposalList;
                if (isNotEmpty(rivalCombinationList) && rivalCombinationList.size() > j) {
                    rivalTeamProposalList = rivalCombinationList.get(j);
                } else {
                    rivalTeamProposalList = new ArrayList<>();
                }
                int randomTeam = randomGenerator(teamProposalList.size());
                TournamentTeamProposal teamProposal = teamProposalList.get(randomTeam);
                //removing chosen team proposal from list
                teamProposalList.remove(teamProposal);
                //add current teamProposal as rival to current composition
                rivalTeamProposalList.add(teamProposal);
                if (rivalTeamProposalList.size() == 1) { //if new rival list, then add it in Round rival combination
                    rivalCombinationList.add(rivalTeamProposalList);
                }
            }
        }
        if (rivalCombinationList.size() < rivalTotalCombinationCount) {
            rivalCombinationList = IntStream.range(1, rivalTotalCombinationCount + 1).parallel()
                    .mapToObj(index -> new ArrayList<TournamentTeamProposal>())
                    .collect(Collectors.toList());
        }
        return rivalCombinationList;
    }

    /**
     * Returns match count for requested parameters of series and primary team count (team proposals)
     */
    private int calculateSeriesCountForRound(double teamProposalListSize, double matchRivalsCount) {
        return (int) sqrtUp(Math.ceil(teamProposalListSize / matchRivalsCount));
    }

    /**
     * Returns the nearest (up) power of two to specified number
     */
    private double sqrtUp(double number) {
        return Math.pow(2, Math.ceil(Math.log(number) / Math.log(2.0)));
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
