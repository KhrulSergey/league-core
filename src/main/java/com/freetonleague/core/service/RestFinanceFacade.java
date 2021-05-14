package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.AccountInfoDto;
import com.freetonleague.core.domain.model.User;

/**
 * Service-facade interface for provide data about user accounts inner DB and process requests (incl. callback from bank-providers) to save data
 */
public interface RestFinanceFacade {

    AccountInfoDto getBalanceByGUID(String GUID, User user);

    AccountInfoDto getBalanceForUser(User user);

    AccountInfoDto getBalanceByUserLeagueId(String leagueId, User user);

    AccountInfoDto getBalanceByTeam(Long teamId, User user);

    AccountInfoDto getBalanceByTournament(Long tournamentId, User user);

    /**
     * Apply coupon by advertisement company hash for user from session
     *
     * @param couponHash advertisement company hash
     * @param user       from current session
     * @return updated Account Balance
     */
    AccountInfoDto applyCouponByHashForUser(String couponHash, User user);
}
