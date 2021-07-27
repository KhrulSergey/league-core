package com.freetonleague.core.service.tournament.implementations;

import com.freetonleague.core.domain.dto.tournament.TournamentRoundSettingDto;
import com.freetonleague.core.domain.enums.tournament.*;
import com.freetonleague.core.domain.model.tournament.*;
import com.freetonleague.core.exception.CustomUnexpectedException;
import com.freetonleague.core.exception.config.ExceptionMessages;
import com.freetonleague.core.service.tournament.TournamentGenerator;
import com.freetonleague.core.service.tournament.TournamentProposalService;
import com.freetonleague.core.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Slf4j
@RequiredArgsConstructor
@Component("survivalEliminationGenerator")
public class TournamentSurvivalEliminationGeneratorImpl implements TournamentGenerator {

    private final TournamentProposalService tournamentProposalService;

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
        log.debug("^ trying to generate rounds for tournament.id '{}' with Survival Elimination algorithm", tournament.getId());
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
        List<TournamentTeamProposal> currentRoundRivalList = tournamentProposalService.getApprovedTeamProposalListByTournament(tournament);
        Integer matchRivalCount = gameDisciplineSettings.getMatchRivalCount();
        Integer matchCountPerSeries = tournamentSettings.getMatchCountPerSeries();
        int currentRoundNumber = 1;

        int roundRivalsListSize = currentRoundRivalList.size();
        if (!this.isValidTeamCountForSurvival(roundRivalsListSize, matchRivalCount)) {
            log.error("!> requesting generate survival tournament rounds for tournament with non-acceptable '{}' count of rivals" +
                    " (approved team proposals). Check evoking clients", roundRivalsListSize);
            return null;
        }

        //generate round, series and matches
        TournamentRound newRound = this.verifyAndGenerateNewRound(currentRoundNumber,
                currentRoundRivalList, matchRivalCount, matchCountPerSeries, new ArrayList<>());
        newRound.setTournament(tournament);
        return Collections.singletonList(newRound);
    }

    /**
     * Compose additional tournament settings based upon tournament template.
     * e.g. Generate default round settings from embedded tournament settings
     */
    @Override
    public TournamentSettings composeAdditionalTournamentSettings(Tournament tournament) {
        //define vars for algorithm
        int roundListCount = tournament.getTournamentRoundList().size();
        TournamentSettings tournamentSettings = tournament.getTournamentSettings();
        int teamProposalListSize = tournamentProposalService.getApprovedTeamProposalListByTournament(tournament).size();
        GameDisciplineSettings gameDisciplineSettings = tournament.getGameDisciplineSettings();
        int seriesRivalKickOffDefaultCount = gameDisciplineSettings.getSeriesRivalKickOffDefaultCount();
        int matchRivalCount = gameDisciplineSettings.getMatchRivalCount();
        int currentRoundNumber = 1;
        int seriesCountInRound;

        Map<Integer, TournamentRoundSettingDto> tournamentRoundSettingsList = Optional.ofNullable(tournamentSettings.getTournamentRoundSettingsList()).orElse(new HashMap<>());
        do {
            if (isNull(tournamentRoundSettingsList.get(currentRoundNumber))) {
                tournamentRoundSettingsList.put(
                        currentRoundNumber,
                        TournamentRoundSettingDto.builder()
                                .roundNumber(currentRoundNumber)
                                .seriesRivalKickOffCount(seriesRivalKickOffDefaultCount)
                                .build());
            }
            seriesCountInRound = this.calculateSeriesCountForRoundFromAllTournamentProposals(currentRoundNumber, teamProposalListSize, matchRivalCount);
            currentRoundNumber++;
        } while (seriesCountInRound > 1 || roundListCount > currentRoundNumber);


        tournamentSettings.setTournamentRoundSettingsList(tournamentRoundSettingsList);
        return tournamentSettings;
    }

    /**
     * Compose series, matches and rivals for next opened tournament round for specified tournament.
     * Look to parents for series in opened tournamentRound and compose rivals.
     * Tournament should have opened Round or available to create new open one.
     */
    @Override
    public TournamentRound composeNextRoundForTournament(Tournament tournament) {
        log.debug("^ trying to compose series and matches for new opened round for tournament.id '{}' with Survival Elimination algorithm",
                tournament.getId());

        //define if composing next round is eligible
        List<TournamentRound> tournamentRoundList = tournament.getTournamentRoundList();
        TournamentRound prevTournamentRound = tournamentRoundList.get(tournamentRoundList.size() - 1);
        if (TournamentStatusType.activeStatusList.contains(prevTournamentRound.getStatus())) {
            log.error("!> requesting composeNewRoundForTournament active previous TournamentRound.number {} for tournament.id {}. " +
                    "Check evoking clients", prevTournamentRound.getRoundNumber(), tournament.getId());
            return null;
        }
        int prevTournamentRoundNumber = prevTournamentRound.getRoundNumber();

        // Get rivals for new round as winners from previous round series
        TournamentSettings tournamentSettings = tournament.getTournamentSettings();
        if (isNull(tournamentSettings.getTournamentRoundSettingsList())) {
            log.error("!> requesting composeNewRoundForTournament for survival round with EMPTY tournament round " +
                    "settings list for tournament.id {}. Check evoking clients", tournament.getId());
            return null;
        }
        //get prev round settings
        TournamentRoundSettingDto tournamentPreviousRoundSettings = tournamentSettings.getTournamentRoundSettingsList()
                .get(prevTournamentRoundNumber);
        if (isNull(tournamentPreviousRoundSettings)) {
            log.error("!> requesting composeNewRoundForTournament for next survival round with NULL tournament-round-settings " +
                            "for previous round.number {} in tournament.id {}. So we can't define newRoundRivalList. Check evoking clients",
                    prevTournamentRoundNumber, tournament.getId());
            return null;
        }
        //series list from prev round
        List<TournamentSeries> prevTournamentSeriesList = prevTournamentRound.getSeriesList();
        //match and series rival count
        int matchRivalCount = tournament.getGameDisciplineSettings().getMatchRivalCount();
        // count series rival from prev series/round to pass in new round
        int seriesRivalKickOffCount = tournamentPreviousRoundSettings.getSeriesRivalKickOffCount();
        int prevSeriesRivalWinnersCount = matchRivalCount - seriesRivalKickOffCount;

        // define winners from prev round and it will be rivals for current round
        List<TournamentTeamProposal> currentRoundRivalList = prevTournamentSeriesList.parallelStream()
                .map(s -> this.filterSeriesRivalWinnersFromSeries(s, prevSeriesRivalWinnersCount))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        int matchCountPerSeries = tournament.getTournamentSettings().getMatchCountPerSeries();
        TournamentRound newRound = this.verifyAndGenerateNewRound(prevTournamentRoundNumber + 1,
                currentRoundRivalList, matchRivalCount, matchCountPerSeries, prevTournamentSeriesList);
        newRound.setTournament(tournament);
        return newRound;
    }

    /**
     * Compose rival for next series (child) by get winner of parent-specified series. If next series is full of rivals then compose matches.
     * Look to specified series - find it's winner X. Look to specified series - find it's child series Y. Compose rival od series Y as winner X.
     */
    @Override
    public TournamentSeries composeRivalForChildTournamentSeries(TournamentSeries tournamentSeries) {
        log.error("!> composeRivalForChildSeriesForTournament not implemented in survivalEliminationGenerator. Just return child (next) series as is");
        return tournamentSeries.getChildSeries();
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
            log.error("!!> requesting composeNewRoundForTournament for errors in series rival winners for series.id '{}'. " +
                            "Series rival winner size is '{}', but expected '{}'. Check stack trace and evoking clients",
                    tournamentSeries.getId(), seriesRivalWinners.size(), matchRivalWinners);
            throw new CustomUnexpectedException(ExceptionMessages.TOURNAMENT_SERIES_GENERATION_ERROR);
        }
        return seriesRivalWinners;
    }

    private TournamentRound verifyAndGenerateNewRound(int currentRoundNumber, List<TournamentTeamProposal> currentRoundRivalList,
                                                      int matchRivalCount, int matchCountPerSeries,
                                                      List<TournamentSeries> parentTournamentSeriesList) {
        int roundRivalsListSize = currentRoundRivalList.size();
        if (!this.isValidTeamCountForSurvival(roundRivalsListSize, matchRivalCount)) {
            log.error("!> requesting generate survival tournament rounds for tournament with non-acceptable '{}' count of rivals" +
                    " (approved team proposals). Check evoking clients", roundRivalsListSize);
            return null;
        }

        //generate round, series and matches
        // count round based upon discipline/tournament settings and teamProposalList size
        int seriesCountInRound = this.calculateSeriesCountForRound(currentRoundRivalList.size(), matchRivalCount);
        // get rival combinations or skip for last round with series count = 1
        List<List<TournamentTeamProposal>> rivalCombinations = this.composeRivalCombinationsFromTeamProposals(seriesCountInRound,
                matchRivalCount, currentRoundRivalList);
        if (isEmpty(rivalCombinations)) {
            log.error("!> round compose with TournamentSurvivalEliminationGeneratorImpl caused error. " +
                    "RivalCombinations is NULL. Check evoking params");
            throw new CustomUnexpectedException(ExceptionMessages.TOURNAMENT_SERIES_GENERATION_ERROR);
        }
        TournamentRound tournamentRound = this.generateRound(currentRoundNumber, rivalCombinations, parentTournamentSeriesList, matchCountPerSeries);
        if (isNull(tournamentRound)) {
            log.error("!> round compose with TournamentSurvivalEliminationGeneratorImpl caused error. " +
                    "TournamentRound generated as NULL. Check stack trace");
            throw new CustomUnexpectedException(ExceptionMessages.TOURNAMENT_SERIES_GENERATION_ERROR);
        }
        return tournamentRound;
    }

    /**
     * Returns generated series for specified rivalCombinations, matchCount, seriesPosition
     * Or prototype with fields: Name and Number
     */
    public TournamentRound generateRound(Integer roundNumber, List<List<TournamentTeamProposal>> rivalCombinations,
                                         List<TournamentSeries> parentTournamentSeriesList, int matchCountPerSeries) {
        //define if current round consist of only 1 series, then it should be the last round in tournament
        boolean isLastRound = rivalCombinations.size() == 1;
        TournamentRound tournamentRound = TournamentRound.builder()
                .name("Round #" + roundNumber)
                .roundNumber(roundNumber)
                .status(TournamentStatusType.CREATED)
                .type(TournamentRoundType.DEFAULT)
                .isLast(isLastRound)
                .build();

        List<TournamentSeries> tournamentSeriesList = this.generateSeriesList(tournamentRound, rivalCombinations,
                parentTournamentSeriesList, matchCountPerSeries);
        tournamentRound.setSeriesList(tournamentSeriesList);
        return tournamentRound;
    }

    /**
     * Returns generated series list for specified tournamentRound, rivalCombinations, matchCount, seriesPosition
     */
    private List<TournamentSeries> generateSeriesList(TournamentRound tournamentRound,
                                                      List<List<TournamentTeamProposal>> rivalCombinations,
                                                      List<TournamentSeries> parentTournamentSeriesList,
                                                      int matchCountPerSeries) {
        if (isEmpty(rivalCombinations)) {
            log.error("!> round generation with TournamentSurvivalEliminationGeneratorImpl caused error. RivalCombinations is NULL. Check evoking params");
            throw new CustomUnexpectedException(ExceptionMessages.TOURNAMENT_SERIES_GENERATION_ERROR);
        }
        return IntStream.range(0, rivalCombinations.size()).parallel()
                .mapToObj(index -> this.generateSeries(tournamentRound,
                        this.getParentSeries(index, parentTournamentSeriesList),
                        rivalCombinations.get(index), matchCountPerSeries)
                )
                .collect(Collectors.toList());
    }

    /**
     * Returns generated series for specified rivalCombinations, matchCount, seriesPosition
     */
    private TournamentSeries generateSeries(TournamentRound tournamentRound, List<TournamentSeries> parentSeriesList,
                                            List<TournamentTeamProposal> rivalCombinations, int matchCount) {
        if (isNull(rivalCombinations)) {
            log.error("!> round generation with TournamentSurvivalEliminationGeneratorImpl caused error. RivalCombinations is NULL. Check evoking params");
            throw new CustomUnexpectedException(ExceptionMessages.TOURNAMENT_SERIES_GENERATION_ERROR);
        }
        // For Survival elimination all series with type 'UPPER_BRACKET'
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
     * Returns series count for requested parameters of round and size of all team proposals to tournament
     */
    private int calculateSeriesCountForRoundFromAllTournamentProposals(int roundIndex, int tournamentTeamProposalsCount,
                                                                       int matchRivalsCount) {
        // team count divide by current round index and by count of rival per match
        return tournamentTeamProposalsCount / (matchRivalsCount * (int) Math.pow(2, roundIndex - 1));
    }

    /**
     * Returns match count for requested parameters of series and primary team count (team proposals)
     */
    private int calculateSeriesCountForRound(int teamProposalListSize, int matchRivalsCount) {
        // team count divide by count of rival per match
        return (teamProposalListSize / matchRivalsCount);
    }

    /**
     * Returns collection of random generated rival teams (TournamentTeamProposal) for further processing
     */
    private List<List<TournamentTeamProposal>> composeRivalCombinationsFromTeamProposals(Integer rivalCombinationCount,
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
