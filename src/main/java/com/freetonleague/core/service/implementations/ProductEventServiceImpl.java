package com.freetonleague.core.service.implementations;

import com.freetonleague.core.cloudclient.TelegramClientService;
import com.freetonleague.core.domain.dto.AccountInfoDto;
import com.freetonleague.core.domain.dto.AccountTransactionInfoDto;
import com.freetonleague.core.domain.dto.EventDto;
import com.freetonleague.core.domain.dto.ProductPurchaseNotificationDto;
import com.freetonleague.core.domain.enums.*;
import com.freetonleague.core.domain.model.Product;
import com.freetonleague.core.domain.model.ProductPurchase;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.ProductManageException;
import com.freetonleague.core.exception.config.ExceptionMessages;
import com.freetonleague.core.mapper.ProductPurchaseMapper;
import com.freetonleague.core.service.EventService;
import com.freetonleague.core.service.FinancialClientService;
import com.freetonleague.core.service.ProductEventService;
import com.freetonleague.core.service.ProductPurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.isNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductEventServiceImpl implements ProductEventService {

    private final FinancialClientService financialClientService;
    private final TelegramClientService telegramClientService;
    private final ProductPurchaseService productPurchaseService;
    private final EventService eventService;
    private final ProductPurchaseMapper purchaseMapper;

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
        log.debug("^ requesting process user purchase payment in Product Event Service for purchase.id '{}', guid '{}', totalAmount '{}'.",
                productPurchase.getId(), productPurchase.getCoreId(), productPurchase.getPurchaseTotalAmount());
        List<AccountTransactionInfoDto> paymentList = null;
        if (productPurchase.getProduct().getAccessType().isPaid()
                && this.needToPaidPurchaseAmount(productPurchase)) {
            log.debug("^ product purchase required payment. Try to call purchase transaction in Product Event Service.");
            paymentList = this.tryMakePurchasePayment(productPurchase);

        }
        return paymentList;
    }

    /**
     * Process product purchase payment
     */
    @Override
    public void processProductPurchaseStateChange(ProductPurchase productPurchase, PurchaseStateType state) {
        log.debug("^ state of product purchase was changed from '{}' to '{}'. Process user state change in Product Event Service.",
                productPurchase.getPrevState(), productPurchase.getState());
        if (state.isApproved()) {
            ProductPurchaseNotificationDto notification = purchaseMapper.toNotification(productPurchase);
            boolean result = telegramClientService.sendNewPurchaseNotification(notification);
            if (!result) {
                this.handleProductPurchaseStateChange(productPurchase, PurchaseStateType.FROZEN);
            }
        }
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

    private void handleProductPurchaseStateChange(ProductPurchase productPurchase, PurchaseStateType newState) {
        log.debug("^ handle changing state '{}' of purchase.id to '{}' in Product Event Service.", productPurchase.getId(), newState);
        Map<String, Object> updateFields = Map.of(
                "state", newState
        );

        EventDto event = EventDto.builder()
                .id(UUID.randomUUID().toString())
                .message("Changed status of Product Purchase")
                .eventOperationType(EventOperationType.UPDATE_FIELDS)
                .eventTopic(EventProducerModelType.PRODUCT_PURCHASE)
                .modelId(productPurchase.getId().toString())
                .modelData(updateFields)
                .createdDate(LocalDate.now())
                .build();
        try {
            eventService.sendEvent(event);
        } catch (Exception exc) {
            log.error("Error in handleStatusChange: '{}'", exc.getMessage());
        }
        //TODO удалить непосредственный вызов изменения данных и разработать обработчик сообщений из Kafka до 01/10/21
        // или удалить коммент
        productPurchase.setState(newState);
        productPurchaseService.editPurchase(productPurchase);
    }
}
