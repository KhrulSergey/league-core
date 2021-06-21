package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.AccountInfoDto;
import com.freetonleague.core.domain.dto.AccountTransactionInfoDto;
import com.freetonleague.core.domain.enums.*;
import com.freetonleague.core.domain.model.Product;
import com.freetonleague.core.domain.model.ProductPurchase;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.ProductManageException;
import com.freetonleague.core.exception.config.ExceptionMessages;
import com.freetonleague.core.service.FinancialClientService;
import com.freetonleague.core.service.ProductEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductEventServiceImpl implements ProductEventService {

    private final FinancialClientService financialClientService;

    @Value("${freetonleague.service.league-finance.product-billing-account-guid}")
    private String productBillingAccountGUID;

    /**
     * Process product status changing
     */
    @Override
    public void processProductStatusChange(Product product, ProductStatusType newProductStatusType) {
        log.debug("^ new status changed for product '{}' with new status '{}'. ProductEventService not implement processing",
                product, newProductStatusType);
    }

    /**
     * Process product purchase payment
     */
    @Override
    public List<AccountTransactionInfoDto> processProductPurchasePayment(ProductPurchase productPurchase) {
        log.debug("^ state of product purchase was changed from '{}' to '{}'. Process user purchase state change in Product Event Service.",
                productPurchase.getPrevState(), productPurchase.getState());
        List<AccountTransactionInfoDto> paymentList = null;
        if (productPurchase.getProduct().getAccessType().isPaid()
                && this.needToPaidPurchaseAmount(productPurchase)) {
            log.debug("^ state of product purchase required payment. Try to call withdraw transaction in Product Event Service.");
            paymentList = this.tryMakePurchasePayment(productPurchase);

        }
        return paymentList;
    }

    /**
     * Returns sign: if prev status was non-active and new status is active we need to debit money from user
     */
    private boolean needToPaidPurchaseAmount(ProductPurchase productPurchase) {
        return (isNull(productPurchase.getPrevState())
                || PurchaseStateType.disabledProposalStateList
                .contains(productPurchase.getPrevState()))
                && PurchaseStateType.activeProposalStateList.contains(productPurchase.getState());
    }

    /**
     * Try to make purchase payment to buy product
     */
    private List<AccountTransactionInfoDto> tryMakePurchasePayment(ProductPurchase productPurchase) {
        User user = productPurchase.getUser();
        Product product = productPurchase.getProduct();
        log.debug("^ try to make purchase payment for product.id '{}' from user.leagueId '{}' to service account.guid '{}'",
                product.getId(), user.getLeagueId(), productBillingAccountGUID);

        double purchaseAmount = productPurchase.getPurchaseTotalAmount();
        AccountInfoDto userAccountDto = financialClientService.getAccountByHolderInfo(user.getLeagueId(),
                AccountHolderType.USER);
        if (userAccountDto.getAmount() < purchaseAmount) {
            log.warn("~ forbiddenException for create purchase from user '{}' for product id '{}' and status '{}'. " +
                            "User doesn't have enough fund to pay purchase amount",
                    user.getLeagueId(), product.getId(), product.getStatus());
            throw new ProductManageException(ExceptionMessages.PRODUCT_PURCHASE_VERIFICATION_ERROR,
                    String.format("User '%s' doesn't have enough fund to pay purchase amount for product.id '%s'. " +
                            "Request rejected.", user.getLeagueId(), product.getId()));
        }

        AccountInfoDto productAccountDto = financialClientService.getAccountByGUID(productBillingAccountGUID);

        AccountTransactionInfoDto result = financialClientService.applyPurchaseTransaction(
                this.composePurchaseAmountPaymentTransaction(userAccountDto, productAccountDto, purchaseAmount));
        if (isNull(result)) {
            log.error("!> error while create new purchase for product.id '{}' from user.id '{}' to service account.guid '{}'. " +
                            "Error while transferring fund to service account. Check requested params.",
                    product.getId(), user.getLeagueId(), productBillingAccountGUID);
            throw new ProductManageException(ExceptionMessages.PRODUCT_PURCHASE_VERIFICATION_ERROR,
                    "Error while transferring fund to pay purchase amount. Check requested params.");
        }
        return Collections.singletonList(result);
    }

    private AccountTransactionInfoDto composePurchaseAmountPaymentTransaction(AccountInfoDto accountSourceDto,
                                                                              AccountInfoDto accountTargetDto,
                                                                              double purchaseAmount) {
        return AccountTransactionInfoDto.builder()
                .amount(purchaseAmount)
                .sourceAccount(accountSourceDto)
                .targetAccount(accountTargetDto)
                .transactionType(TransactionType.PAYMENT)
                .transactionTemplateType(TransactionTemplateType.PRODUCT_PURCHASE)
                .status(AccountTransactionStatusType.FINISHED)
                .build();
    }
}
