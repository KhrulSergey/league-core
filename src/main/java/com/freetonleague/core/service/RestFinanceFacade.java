package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.AccountInfoDto;
import com.freetonleague.core.domain.dto.AccountTransactionInfoDto;
import com.freetonleague.core.domain.dto.MPubgTonExchangeAmountDto;
import com.freetonleague.core.domain.enums.AccountTransactionStatusType;
import com.freetonleague.core.domain.filter.MPubgTonWithdrawalCreationFilter;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

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
     * Returns found transaction by specified GUID or Null
     *
     * @param transactionGUID identifier of transaction to search
     * @param user            current user from session
     * @return updated withdraw transaction info
     */
    AccountTransactionInfoDto getTransactionByGUID(String transactionGUID, User user);

    /**
     * Returns found transaction history (list) for current user
     *
     * @param pageable   filtered params to search transactions
     * @param statusList status list to filter transactions
     * @param user       current user from session
     * @return transaction list filtered by specified params
     */
    Page<AccountTransactionInfoDto> getMyTransactionsHistory(Pageable pageable, List<AccountTransactionStatusType> statusList, User user);

    /**
     * Returns found transaction history (list) by specified params (only for admin)
     *
     * @param pageable   filtered params to search transactions
     * @param statusList status list to filter transactions
     * @param leagueId   leagueId of user to filter transactions
     * @param user       current user from session
     * @return transaction list filtered by specified params
     */
    Page<AccountTransactionInfoDto> getTransactionsHistory(Pageable pageable, String leagueId, List<AccountTransactionStatusType> statusList, User user);

    /**
     * Returns created withdraw fund transaction info (with pause status) for specified params
     *
     * @param amount            amount of withdraw
     * @param sourceAccountGUID identifier of account to withdraw fund
     * @param targetAddress     identifier of target account (to transfer fund)
     * @param user              current user from session
     * @return withdraw fund transaction info
     */
    AccountTransactionInfoDto createWithdrawRequest(Double amount, String sourceAccountGUID, String targetAddress, User user);

    /**
     * Returns edited withdraw fund transaction info (with pause status) for specified params (only for admin)
     *
     * @param transactionInfoDto new data of transactio
     * @param user               current user from session
     * @return updated withdraw transaction info
     */
    AccountTransactionInfoDto editWithdrawRequest(String transactionGUID, AccountTransactionInfoDto transactionInfoDto, User user);

    /**
     * Returns updated info of canceled withdraw transaction (not implemented)
     *
     * @param transactionGUID identifier of transaction
     * @param user            current user from session
     * @return cancelled withdraw transaction info
     */
    AccountTransactionInfoDto cancelWithdrawRequest(String transactionGUID, User user);

    /**
     * Apply coupon by advertisement company hash for user from session
     *
     * @param couponHash advertisement company hash
     * @param user       from current session
     * @return updated Account Balance
     */
    AccountInfoDto applyCouponByHashForUser(String couponHash, User user);

    MPubgTonExchangeAmountDto getMPubgExchangeAmountForTon(Double tonAmount);

    void createMPubgWithdrawalTransaction(MPubgTonWithdrawalCreationFilter filter, User user);

}
