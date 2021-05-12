package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.AccountInfoDto;
import com.freetonleague.core.domain.enums.AccountHolderType;
import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.Tournament;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.ExceptionMessages;
import com.freetonleague.core.exception.TeamManageException;
import com.freetonleague.core.exception.UnauthorizedException;
import com.freetonleague.core.security.permissions.CanManageSystem;
import com.freetonleague.core.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.util.Objects.isNull;

/**
 * Service-facade for provide data from inner DB and process requests (incl. callback from bank-providers) to save data
 * Also Validate request and incoming data
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RestFinanceFacadeImpl implements RestFinanceFacade {

    //call core-service to get data about user accounting
    private final FinancialClientService financialClientService;
    private final RestTournamentFacade restTournamentFacade;
    private final RestTeamFacade restTeamFacade;
    private final RestUserFacade restUserFacade;

    @Override
    public AccountInfoDto getBalanceByGUID(String GUID, User user) {
        return this.getVerifiedAccountByGUID(GUID, user, true);
    }

    @Override
    public AccountInfoDto getBalanceForUser(User user) {
        if (isNull(user)) {
            log.debug("^ user is not authenticate. 'getBalanceForUser' in RestFinanceFacade request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'getBalanceForUser' request denied");
        }
        return this.getVerifiedAccountByHolder(user.getLeagueId(), AccountHolderType.USER, user, false);
    }

    @CanManageSystem
    @Override
    public AccountInfoDto getBalanceByUserLeagueId(String leagueId, User user) {
        User userByLeagueId = restUserFacade.getVerifiedUserByLeagueId(leagueId);
        return this.getVerifiedAccountByHolder(userByLeagueId.getLeagueId(), AccountHolderType.USER, user, true);
    }

    @Override
    public AccountInfoDto getBalanceByTeam(Long teamId, User user) {
        Team team = restTeamFacade.getVerifiedTeamById(teamId, user, true);
        return this.getVerifiedAccountByHolder(team.getCoreId(), AccountHolderType.TEAM, user, true);
    }

    @CanManageSystem
    @Override
    public AccountInfoDto getBalanceByTournament(Long tournamentId, User user) {
        Tournament tournament = restTournamentFacade.getVerifiedTournamentById(tournamentId);
        return this.getVerifiedAccountByHolder(tournament.getCoreId(), AccountHolderType.TOURNAMENT, user, true);
    }

    /**
     * Returns account info by account GUID and user with privacy check
     */
    public AccountInfoDto getVerifiedAccountByGUID(String GUID, User user, boolean checkUser) {
        if (checkUser && isNull(user)) {
            log.debug("^ user is not authenticate. 'getVerifiedAccount' in RestFinanceFacade request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'getVerifiedAccount' request denied");
        }
        AccountInfoDto account = financialClientService.getAccountByGUID(GUID);
        if (isNull(account)) {
            log.debug("^ Account for requested GUID {} was not found. 'getVerifiedAccount' in RestFinanceFacade request denied", GUID);
            throw new TeamManageException(ExceptionMessages.ACCOUNT_INFO_NOT_FOUND_ERROR, "Account with requested id " + GUID + " was not found");
        }
        return account;
    }

    /**
     * Returns account info by account holder type, holder GUID and user with privacy check
     */
    private AccountInfoDto getVerifiedAccountByHolder(UUID GUID, AccountHolderType accountHolderType, User user, boolean checkUser) {
        if (checkUser && isNull(user)) {
            log.debug("^ user is not authenticate. 'getVerifiedAccount' in RestFinanceFacade request denied");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_ERROR, "'getVerifiedAccount' request denied");
        }
        AccountInfoDto account = financialClientService.getAccountByHolderInfo(GUID, accountHolderType);
        if (isNull(account)) {
            log.debug("^ Account for requested GUID {} was not found. 'getVerifiedAccount' in RestFinanceFacade request denied", GUID);
            throw new TeamManageException(ExceptionMessages.ACCOUNT_INFO_NOT_FOUND_ERROR, "Account with requested id " + GUID + " was not found");
        }
        return account;
    }
}
