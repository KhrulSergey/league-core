package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.enums.TournamentMatchRivalParticipantStatusType;
import com.freetonleague.core.domain.enums.TournamentRoundType;
import com.freetonleague.core.domain.enums.TournamentSeriesBracketType;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.model.*;
import com.freetonleague.core.exception.CustomUnexpectedException;
import com.freetonleague.core.exception.ExceptionMessages;
import com.freetonleague.core.service.TournamentGenerator;
import com.freetonleague.core.service.TournamentProposalService;
import com.freetonleague.core.service.TournamentRoundService;
import com.freetonleague.core.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
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
        Integer matchRivalCountLimit = gameDisciplineSettings.getMatchRivalCount();
        Integer matchCountPerSeries = tournamentSettings.getMatchCountPerSeries();
        int teamProposalListSize = teamProposalList.size();

        if (!this.isPowerOfN(teamProposalListSize, matchRivalCountLimit)) {
            log.error("!> requesting generate tournament round for tournament with non-acceptable '{}' count of rivals" +
                    " (approved team proposals). Check evoking clients", teamProposalListSize);
            return null;
        }

        int roundsCount;
        int currentRoundNumber = 1;
        List<List<TournamentTeamProposal>> rivalCombinations;
        List<TournamentSeries> parentTournamentSeriesList = new ArrayList<>();
        List<TournamentRound> newTournamentRoundList = new ArrayList<>();

        //generate round, series and matches
        //for round #2+ series(and inner matches) will be only prototypes, they will be filled with rivals as soon as first round finish
        do {
            // count round based upon discipline/tournament settings and teamProposalList size
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

        // compose updates series list with filled rivals
        List<TournamentSeries> updatedTournamentSeriesList = tournamentRound.getSeriesList().parallelStream()
                .map(this::composeAndFillRivalsOfTournamentSeries).filter(Objects::nonNull).collect(Collectors.toList());
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
    private TournamentSeries composeAndFillRivalsOfTournamentSeries(TournamentSeries currentSeries) {
        if (isNotEmpty(currentSeries.getSeriesRivalList())) {
            log.error("!> requesting composeAndFillRivalsOfTournamentSeries in composeNewRoundForTournament for not empty " +
                    "list of series rival for series '{}'. Check evoking clients.", currentSeries);
            return null;
        }
        // collect and build series rival for current series
        List<TournamentSeriesRival> rivalList = currentSeries.getParentSeriesList().stream()
                .map(parentSeries -> this.generateSeriesRival(currentSeries, parentSeries, parentSeries.getTeamProposalWinner()))
                .collect(Collectors.toList());
        // update series rivals in current series
        currentSeries.setSeriesRivalList(rivalList);
        // update match list for series and fill each match with rivals (from TournamentSeriesRival list above)
        List<TournamentMatch> newTournamentMatchList = currentSeries.getMatchList().parallelStream()
                .peek(match -> match.setMatchRivalList(rivalList.parallelStream()
                        .map(rival -> this.generateMatchRival(match, rival.getTeamProposal()))
                        .collect(Collectors.toList()))).collect(Collectors.toList());

        currentSeries.setMatchList(newTournamentMatchList);
        return currentSeries;
    }

    /**
     * Returns generated series for specified rivalCombinations, matchCount, seriesPosition
     */
    private TournamentRound generateRound(List<List<TournamentTeamProposal>> rivalCombinations,
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
                .status(TournamentStatusType.CREATED)
                .type(TournamentRoundType.DEFAULT)
                .isLast(isLastRound)
                .build();

        List<TournamentSeries> tournamentSeriesList = IntStream.range(0, rivalCombinations.size()).parallel()
                .mapToObj(index -> this.generateSeries(tournamentRound,
                        this.getParentSeries(index, parentTournamentSeriesList),
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
    private int calculateSeriesCountForRound(int teamProposalListSize, Integer matchRivalsCount) {
        // team count divide by count of rival per match
        return (teamProposalListSize / matchRivalsCount);
    }

    /**
     * Returns collection of random generated rival teams (TournamentTeamProposal) for further processing
     */
    private List<List<TournamentTeamProposal>> composeRivalCombinationsFromTeamProposal(Integer rivalCombinationCount,
                                                                                        Integer rivalCountPerMatch,
                                                                                        List<TournamentTeamProposal> teamProposalList) {
        if (isNull(rivalCombinationCount) || isNull(rivalCountPerMatch) || isNull(teamProposalList)) {
            log.error("!> requesting generate rival combination for NULL rivalCombinationCount '{}' " +
                            "or NULL matchRivalCount '{}' or NULL teamProposalList '{}'. Check evoking clients",
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
