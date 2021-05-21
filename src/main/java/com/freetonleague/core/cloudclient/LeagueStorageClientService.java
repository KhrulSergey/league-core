package com.freetonleague.core.cloudclient;

import com.freetonleague.core.domain.dto.MediaResourceDto;
import com.freetonleague.core.domain.enums.ResourcePrivacyType;
import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.Tournament;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

/**
 * Service for save and get data from a League-Storage module
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LeagueStorageClientService {

    private final LeagueStorageClientCloud leagueStorageClientCloud;

    @Value("${freetonleague.service.league-storage.service-token}")
    private String leagueIdServiceToken;

    /**
     * Returns hashKey of saved team logo (media resource)
     */
    public String saveTeamLogo(Team team) {
        if (isNull(team)) {
            log.error("!> requesting saveTeamLogo for NULL team. Check evoking clients");
        }
        log.debug("^ try to saveTeamLogo in LeagueStorageClientService for team.id {}", team.getCoreId());
        MediaResourceDto teamLogo = MediaResourceDto.builder()
                .rawData(team.getLogoRawFile())
                .name(String.format("Logo-for-team-%s", team.getName()))
                .creatorGUID(team.getCoreId())
                .privacyType(ResourcePrivacyType.OPEN)
                .build();
        teamLogo = leagueStorageClientCloud.saveMediaResource(leagueIdServiceToken, teamLogo);
        return teamLogo.getHashKey();
    }

    /**
     * Returns hashKey of saved tournament logo image (media resource)
     */
    public String saveTournamentLogo(Tournament tournament) {
        if (isNull(tournament)) {
            log.error("!> requesting saveTournamentLogo for NULL tournament. Check evoking clients");
        }
        log.debug("^ try to saveTournamentLogo in LeagueStorageClientService for tournament.id {}", tournament.getCoreId());

        MediaResourceDto tournamentLogo = MediaResourceDto.builder()
                .rawData(tournament.getLogoRawFile())
                .name(String.format("Logo-for-tournament-%s", tournament.getName()))
                .creatorGUID(tournament.getCoreId())
                .privacyType(ResourcePrivacyType.OPEN)
                .build();

        tournamentLogo = leagueStorageClientCloud.saveMediaResource(leagueIdServiceToken, tournamentLogo);
        return tournamentLogo.getHashKey();
    }
}
