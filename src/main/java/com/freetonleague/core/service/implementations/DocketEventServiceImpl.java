package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.AccountInfoDto;
import com.freetonleague.core.domain.dto.AccountTransactionInfoDto;
import com.freetonleague.core.domain.enums.*;
import com.freetonleague.core.domain.model.AccountTransaction;
import com.freetonleague.core.domain.model.Docket;
import com.freetonleague.core.domain.model.DocketUserProposal;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.DocketManageException;
import com.freetonleague.core.exception.TeamParticipantManageException;
import com.freetonleague.core.exception.config.ExceptionMessages;
import com.freetonleague.core.service.DocketEventService;
import com.freetonleague.core.service.DocketProposalService;
import com.freetonleague.core.service.FinancialClientService;
import com.freetonleague.core.service.financeUnit.FinancialUnitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class DocketEventServiceImpl implements DocketEventService {

    private final FinancialClientService financialClientService;
    private final DocketProposalService docketProposalService;

    private final FinancialUnitService financialUnitService;

    @Value("${freetonleague.docket.first_ton_docket_id:#{null}}")
    private Long firstTonCompanyDocketId;

    @Value("${freetonleague.docket.first_ton_bonus_amount:1}")
    private Double firstTonCompanyPaymentAmount;

    /**
     * Process docket status changing
     */
    @Override
    public void processDocketStatusChange(Docket docket, DocketStatusType newDocketStatusType) {
        log.debug("^ new status changed for docket '{}' with new status '{}'.", docket, newDocketStatusType);
        if (newDocketStatusType.isCreated()) {
            financialClientService.createAccountByHolderInfo(docket.getCoreId(),
                    AccountHolderType.DOCKET, docket.getName());
        }
    }

    /**
     * Process user proposal to docket status changing
     */
    @Override
    public List<AccountTransactionInfoDto> processDocketUserProposalStateChange(DocketUserProposal docketUserProposal,
                                                                                ParticipationStateType newUserProposalState) {
        log.debug("^ state of user proposal to docket was changed from '{}' to '{}'. Process user proposal state change in Docket Event Service.",
                docketUserProposal.getPrevState(), newUserProposalState);
        List<AccountTransactionInfoDto> paymentList = null;
        Docket docket = docketUserProposal.getDocket();
        if (docket.getAccessType().isPaid()
                && this.needToPaidParticipationFee(docketUserProposal)) {
            log.debug("^ state of docket proposal required participation fee purchase. " +
                    "Try to call purchase (withdraw) transaction from user in DocketEventService.");
            paymentList = this.tryMakeParticipationFeePayment(docketUserProposal);
        }
        if (nonNull(firstTonCompanyDocketId) && firstTonCompanyDocketId.equals(docket.getId())) {
            log.debug("^ try to make bonus payment to docket.id '{}' participant.user.guid '{}' of amount '{}'.", firstTonCompanyDocketId,
                    docketUserProposal.getUser().getLeagueId(), firstTonCompanyPaymentAmount);
            AccountTransaction accountTransaction = AccountTransaction.builder()
                    .amount(firstTonCompanyPaymentAmount)
                    .targetAccount(financialUnitService.getAccountByHolderExternalGUIDAndType(docketUserProposal.getUser().getLeagueId(), AccountHolderType.USER))
                    .transactionType(TransactionType.DEPOSIT)
                    .transactionTemplateType(TransactionTemplateType.EXTERNAL_PROVIDER)
                    .status(AccountTransactionStatusType.FINISHED)
                    .build();

            financialUnitService.createTransaction(accountTransaction);
        }
        return paymentList;
    }

    /**
     * Returns sign: if prev status was non-active and new status is active we need to debit money from user
     */
    private boolean needToPaidParticipationFee(DocketUserProposal docketUserProposal) {
        return (isNull(docketUserProposal.getPrevState())
                || ParticipationStateType.disabledProposalStateList
                .contains(docketUserProposal.getPrevState()))
                && ParticipationStateType.activeProposalStateList.contains(docketUserProposal.getState());
    }

    /**
     * Try to make participation fee and commission from user to docket
     */
    private List<AccountTransactionInfoDto> tryMakeParticipationFeePayment(DocketUserProposal docketUserProposal) {
        User user = docketUserProposal.getUser();
        Docket docket = docketUserProposal.getDocket();
        log.debug("^ try to make participation fee to docket.id '{}' from user.leagueId '{}'",
                docket.getId(), user.getLeagueId());


        double userParticipationFeeAmount = docketProposalService.calculateUserParticipationFee(docketUserProposal);
        AccountInfoDto userAccountDto = financialClientService.getAccountByHolderInfo(user.getLeagueId(),
                AccountHolderType.USER);
        if (userAccountDto.getAmount() < userParticipationFeeAmount) {
            log.warn("~ forbiddenException for create new proposal for user '{}' to docket id '{}' and status '{}'. " +
                            "User doesn't have enough fund to pay participation fee",
                    user.getLeagueId(), docket.getId(), docket.getStatus());
            throw new TeamParticipantManageException(ExceptionMessages.DOCKET_USER_PROPOSAL_VERIFICATION_ERROR,
                    String.format("User '%s' doesn't have enough fund to pay participation fee to docket.id '%s'. " +
                            "Request rejected.", user.getLeagueId(), docket.getId()));
        }

        AccountInfoDto docketAccountDto = financialClientService.getAccountByHolderInfo(
                docket.getCoreId(), AccountHolderType.DOCKET);

        AccountTransactionInfoDto result = financialClientService.applyPurchaseTransaction(
                this.composeParticipationFeeTransaction(userAccountDto, docketAccountDto, userParticipationFeeAmount));
        if (isNull(result)) {
            log.warn("~ forbiddenException for create new proposal for user.id '{}' to docket id '{}'. " +
                            "Error while transferring fund to pat participation fee. Check requested params.",
                    user.getLeagueId(), docket.getId());
            throw new DocketManageException(ExceptionMessages.DOCKET_USER_PROPOSAL_VERIFICATION_ERROR,
                    "Error while transferring fund to pay participation fee. Check requested params.");
        }
        return Collections.singletonList(result);
    }

    private AccountTransactionInfoDto composeParticipationFeeTransaction(AccountInfoDto accountSourceDto,
                                                                         AccountInfoDto accountTargetDto,
                                                                         double docketParticipationFeeAmount) {
        return AccountTransactionInfoDto.builder()
                .amount(docketParticipationFeeAmount)
                .sourceAccount(accountSourceDto)
                .targetAccount(accountTargetDto)
                .transactionType(TransactionType.PAYMENT)
                .transactionTemplateType(TransactionTemplateType.DOCKET_ENTRANCE_FEE)
                .status(AccountTransactionStatusType.FINISHED)
                .build();
    }
}
