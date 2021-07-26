package com.freetonleague.core.service.financeUnit.implementations;

import com.freetonleague.core.domain.dto.finance.ExchangeRatioResponseDto;
import com.freetonleague.core.domain.dto.finance.PaymentInvoiceDto;
import com.freetonleague.core.domain.enums.finance.Currency;
import com.freetonleague.core.domain.enums.finance.*;
import com.freetonleague.core.domain.model.finance.Account;
import com.freetonleague.core.domain.model.finance.AccountTransaction;
import com.freetonleague.core.domain.model.finance.ExchangeOrder;
import com.freetonleague.core.domain.model.finance.ExchangeRatio;
import com.freetonleague.core.exception.FinancialUnitManageException;
import com.freetonleague.core.exception.config.ExceptionMessages;
import com.freetonleague.core.repository.finance.ExchangeOrderRepository;
import com.freetonleague.core.repository.finance.ExchangeRatioRepository;
import com.freetonleague.core.service.financeUnit.FinancialExchangeCurrencyService;
import com.freetonleague.core.service.financeUnit.FinancialUnitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

/**
 * Service for manage currency exchange rates and orders with DB access
 */
@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialExchangeCurrencyServiceImpl implements FinancialExchangeCurrencyService {

    private final FinancialUnitService financialUnitService;
    private final PaymentGatewayClientService paymentGatewayClientService;// call payment gateway
    private final CurrencyMarketClientService currencyMarketClientService;// call currency market provider
    private final ExchangeOrderRepository exchangeOrderRepository;
    private final ExchangeRatioRepository exchangeRatioRepository;
    private final Validator validator;

    @Value("${freetonleague.service.league-finance.exchange-order-expired-timeout-in-sec:600}")
    private Long exchangeOrderExpiredTimeoutInSec;

    @Value("${freetonleague.service.league-finance.exchange-ratio-expired-timeout-in-sec:600}")
    private Long exchangeRatioExpiredTimeoutInSec;

    @Value("${freetonleague.service.league-finance.exchange-order-billing-account-guid}")
    private String exchangeOrderBillingAccountGUID;

    /**
     * Calculate rates for specified currency by request new info from external provider
     */
    @Override
    public ExchangeRatio getExchangeCurrencyRate(Currency currencyToBuy, Currency currencyToSell) {
        if (isNull(currencyToBuy) || isNull(currencyToSell)) {
            log.error("!> requesting getExchangeCurrencyRate for NULL currencyToBuy '{}' or NULL currencyToSell '{}'. " +
                    "Check evoking clients", currencyToBuy, currencyToSell);
            return null;
        }
        log.debug("^ try to find exchange currency rate for currencyToBuy '{}' and currencyToSell '{}' from DB",
                currencyToBuy, currencyToSell);
        CurrencyPairType currencyPairType = CurrencyPairType.getByCurrencies(currencyToBuy, currencyToSell);
        ExchangeRatio exchangeRatio = exchangeRatioRepository.getActiveRatioByCurrencies(currencyToBuy, currencyToSell);
        exchangeRatioRepository.findByCurrencyToBuyAndCurrencyToSellAndExpiredAtAfter(currencyToBuy, currencyToSell, LocalDateTime.now());
        if (isNull(exchangeRatio)) {
            log.debug("^ exchange currency rate for currencyToBuy '{}' and currencyToSell '{}' was not found in DB. Try to calculate.",
                    currencyToBuy, currencyToSell);
            exchangeRatio = this.calculateExchangeCurrencyRate(currencyPairType);
        }
        return exchangeRatio;
    }

    /**
     * Returns found exchange order by guid
     */
    @Override
    public ExchangeOrder getExchangeOrderByGuid(UUID orderGUID) {
        if (isNull(orderGUID)) {
            log.error("!> requesting getExchangeOrderByGuid for NULL orderGUID. Check evoking clients");
            return null;
        }
        log.debug("^ try to get exchange currency order by guid '{}'.", orderGUID);
        return exchangeOrderRepository.findByGUID(orderGUID);
    }

    /**
     * Save exchange currency order with setting additional system settings
     */
    @Override
    public ExchangeOrder createExchangeOrder(ExchangeOrder exchangeOrder) {
        if (isNull(exchangeOrder)) {
            log.error("!> requesting createExchangeOrder for NULL exchangeOrder. Check evoking clients");
            return null;
        }
        Set<ConstraintViolation<ExchangeOrder>> violations = validator.validate(exchangeOrder);
        if (!violations.isEmpty()) {
            log.error("!> requesting createExchangeOrder for exchangeOrder '{}' with constraint violations: '{}'. " +
                    "Check evoking clients", exchangeOrder, violations);
            return null;
        }
        exchangeOrder.generateGUID();

        if (exchangeOrder.getCurrencyToBuy().isTon()) {
            PaymentInvoiceDto paymentInvoice = this.composePaymentInvoiceToBuyTon(exchangeOrder);
            paymentInvoice = paymentGatewayClientService.createPaymentInvoice(paymentInvoice);
            exchangeOrder.setPaymentGateway(paymentInvoice.getGatewayType());
            exchangeOrder.setPaymentUrl(paymentInvoice.getPaymentUrl());
            exchangeOrder.setPaymentInvoiceRaw(paymentInvoice);
        }
        exchangeOrder.setExpiredAt(LocalDateTime.now().plusSeconds(exchangeOrderExpiredTimeoutInSec));
        return exchangeOrderRepository.save(exchangeOrder);
    }

    /**
     * Approve exchange currency order with creating transaction of fund
     *
     * @return ExchangeOrder with status FINISHED and embedded transaction
     * of fund to client account OR order with CANCELED status if some error occurred
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public ExchangeOrder approveExchangeOrder(ExchangeOrder exchangeOrder) {
        if (isNull(exchangeOrder)) {
            log.error("!> requesting createExchangeOrder for NULL exchangeOrder. Check evoking clients");
            return null;
        }
        if (!this.isExistsOrderByGUID(exchangeOrder.getGUID())) {
            log.error("!!> requesting approve exchange order '{}' for non-existed order. Check evoking clients", exchangeOrder.getGUID());
            return null;
        }
        log.debug("^ try to approve exchange order '{}' by get purchase info from payment gateway '{}'.",
                exchangeOrder, exchangeOrder.getPaymentGateway());
        AccountTransaction accountTransaction = this.composeExchangeCurrencyTransaction(exchangeOrder);
        accountTransaction = financialUnitService.createTransaction(accountTransaction);
        if (isNull(accountTransaction)) {
            log.error("!!> approve exchange order cause error while saving transaction for clientAccount '{}', of order '{}'.",
                    exchangeOrder.getClientAccount(), exchangeOrder);
            throw new FinancialUnitManageException(ExceptionMessages.FINANCE_UNIT_TRANSACTION_CREATION_ERROR,
                    "Request to create transaction for approve exchange order.guid" + exchangeOrder.getGUID() +
                            "returned NULL transaction. Request to approve exchange order was denied.");
        }
        exchangeOrder.setStatus(ExchangeOrderStatus.FINISHED);
        exchangeOrder.setPaymentTransaction(accountTransaction);
        return exchangeOrderRepository.save(exchangeOrder);
    }

    private boolean isExistsOrderByGUID(UUID orderGUID) {
        log.debug("^ try check if exchange order exists by guid '{}'.", orderGUID);
        return exchangeOrderRepository.existsByGUID(orderGUID);
    }

    private ExchangeRatio calculateExchangeCurrencyRate(CurrencyPairType currencyPairType) {
        log.debug("^ try to calculate exchange currency rate for currencyPairType '{}' from currency market info.",
                currencyPairType);
        ExchangeRatio exchangeRatio;
        if (currencyPairType.isExistOnMarket()) {
            log.debug("^ calculating exchange rate for currencyPairType '{}' that exist on market. Just get market info.",
                    currencyPairType);
            exchangeRatio = this.loadExchangeCurrencyRateFromMarket(currencyPairType);
        } else {
            log.debug("^ calculating exchange rate for complex currencyPairType '{}' that doesn't exist on market. " +
                    "Compose currency exchange sequence.", currencyPairType);
            List<CurrencyPairType> currencyPairExchangeSequence = this.composeCurrencyExchangeSequenceTemplate(currencyPairType);
            List<ExchangeRatio> exchangeRationSequence = currencyPairExchangeSequence.stream().map(this::loadExchangeCurrencyRateFromMarket)
                    .filter(Objects::nonNull).collect(Collectors.toCollection(LinkedList::new));
            if (currencyPairExchangeSequence.size() != exchangeRationSequence.size()) {
                log.error("!> error while calculateExchangeCurrencyRate composed currency sequence size is not match " +
                        "loaded currency rate from market. Rate for currencyPairType '{}' was not calculated", currencyPairType);
                return null;
            }
            exchangeRatio = ExchangeRatio.builder()
                    .currencyPairType(currencyPairType)
                    .currencyToBuy(currencyPairType.getCurrencyToBuy())
                    .currencyToSell(currencyPairType.getCurrencyToSell())
                    .ratio(this.calculateExchangeCurrencyRateFromSequence(exchangeRationSequence))
                    .provider(CurrencyMarketProviderType.INNER)
                    .parentExchangeRatioList(exchangeRationSequence)
                    .expiredAt(LocalDateTime.now().plusSeconds(exchangeRatioExpiredTimeoutInSec))
                    .build();
        }
        return exchangeRatio;
    }

    /**
     * Calculate exchange rate based upon currency exchange sequence
     * Multiply all ratios between themselves
     */
    private Double calculateExchangeCurrencyRateFromSequence(List<ExchangeRatio> exchangeRatioList) {
        return exchangeRatioList.stream().map(ExchangeRatio::getRatio).reduce(1.0, (x, y) -> x * y);
    }

    /**
     * Compose currency exchange sequence by apply according to the existing static template
     */
    private List<CurrencyPairType> composeCurrencyExchangeSequenceTemplate(CurrencyPairType currencyPairType) {
        log.debug("^ try to compose currency exchange sequence template for complex currencyPairType '{}'.",
                currencyPairType);
        List<CurrencyPairType> currencyPairExchangeSequence = null;
        if (currencyPairType.getCurrencyToBuy().isTon()) {
            //e.g. RUB -> USD, USD -> TON
            currencyPairExchangeSequence = new LinkedList<>();
            currencyPairExchangeSequence.add(CurrencyPairType.getByCurrencies(Currency.USDT, currencyPairType.getCurrencyToSell()));
            currencyPairExchangeSequence.add(CurrencyPairType.TON_USDT);
        } else if (currencyPairType.getCurrencyToSell().isTon()) {
            //e.g. TON -> USD, USD -> RUB
            currencyPairExchangeSequence = new LinkedList<>();
            currencyPairExchangeSequence.add(CurrencyPairType.USDT_TON);
            currencyPairExchangeSequence.add(CurrencyPairType.getByCurrencies(currencyPairType.getCurrencyToBuy(), Currency.USDT));
        }
        return currencyPairExchangeSequence;
    }

    /**
     * Load currency exchange rate from market info service (manager for cloud providers)
     */
    private ExchangeRatio loadExchangeCurrencyRateFromMarket(CurrencyPairType currencyPairType) {
        if (isNull(currencyPairType)) {
            log.error("!> error to loadExchangeCurrencyRateFromMarket for NULL currencyPairType. Check evoking client");
            return null;
        }
        log.debug("^ try to load exchange currency rate from market for currencyPairType '{}'.", currencyPairType);
        ExchangeRatioResponseDto exchangeRatioMarketResponse = currencyMarketClientService.
                getExchangeRateForCurrencies(currencyPairType);
        //e.g TON_USDT: toBuy -> Currency.TON, toSell -> Currency.USDT, CurrencyPairDirectionType.FORWARD, ratio =  1 $ / ASK price = TON amount
        return ExchangeRatio.builder()
                .currencyPairType(currencyPairType)
                .currencyToBuy(currencyPairType.getCurrencyToBuy())
                .currencyToSell(currencyPairType.getCurrencyToSell())
                .ratio(exchangeRatioMarketResponse.getRatio())
                .provider(exchangeRatioMarketResponse.getProvider())
                .exchangeCurrencyRateRawData(exchangeRatioMarketResponse.getRawData())
                .expiredAt(LocalDateTime.now().plusSeconds(exchangeRatioExpiredTimeoutInSec))
                .build();
    }

    /**
     * Compose financial transaction to confirm payment and finishing of exchange order
     */
    private AccountTransaction composeExchangeCurrencyTransaction(ExchangeOrder exchangeOrder) {
        log.debug("^ try to compose exchange currency transaction for exchange order '{}'", exchangeOrder);
        Account billingAccount = financialUnitService.getAccountByGUID(UUID.fromString(exchangeOrderBillingAccountGUID));
        if (isNull(billingAccount)) {
            log.error("!!> compose exchange currency transaction cause error because billingAccount is NULL. " +
                    "composeExchangeCurrencyTransaction for order '{}' was denied.", exchangeOrder);
            throw new FinancialUnitManageException(ExceptionMessages.FINANCE_UNIT_TRANSACTION_CREATION_ERROR,
                    "Request to create transaction for approve exchange order.guid" + exchangeOrder.getGUID() +
                            "was denied. Billing account is not set.");
        }
        AccountTransaction accountTransaction = AccountTransaction.builder()
                .transactionTemplateType(AccountTransactionTemplateType.PAYMENT_GATEWAY)
                .status(AccountTransactionStatusType.FINISHED)
                .build();
        if (exchangeOrder.getCurrencyToBuy().isTon()) {
            accountTransaction.setSourceAccount(billingAccount);
            accountTransaction.setTargetAccount(exchangeOrder.getClientAccount());
            accountTransaction.setAmount(exchangeOrder.getAmountToBuy());
            accountTransaction.setTransactionType(AccountTransactionType.DEPOSIT);
            accountTransaction.setDescription(
                    String.format("Exchanged '%s' TON by payment '%s' %s with gateway %s for exchange order.guid '%s'",
                            exchangeOrder.getAmountToBuy(), exchangeOrder.getAmountToSell(), exchangeOrder.getCurrencyToSell(),
                            exchangeOrder.getPaymentGateway(), exchangeOrder.getGUID()));

        } else if (exchangeOrder.getCurrencyToSell().isTon()) {
            accountTransaction.setTargetAccount(billingAccount);
            accountTransaction.setSourceAccount(exchangeOrder.getClientAccount());
            accountTransaction.setAmount(exchangeOrder.getAmountToSell());
            accountTransaction.setTransactionType(AccountTransactionType.WITHDRAW);
            accountTransaction.setDescription(
                    String.format("Exchanged '%s' %s by selling '%s' TON with gateway %s for exchange order.guid '%s'",
                            exchangeOrder.getAmountToBuy(), exchangeOrder.getCurrencyToBuy(), exchangeOrder.getAmountToSell(),
                            exchangeOrder.getPaymentGateway(), exchangeOrder.getGUID()));
        }
        return accountTransaction;
    }

    private PaymentInvoiceDto composePaymentInvoiceToBuyTon(ExchangeOrder exchangeOrder) {
        return PaymentInvoiceDto.builder()
                .productGUID(exchangeOrder.getGUID())
                .amount(exchangeOrder.getAmountToSell())
                .description(String.format("Exchanged '%s' TON by payment '%s' %s for exchange order.guid '%s'",
                        exchangeOrder.getAmountToBuy(), exchangeOrder.getAmountToSell(), exchangeOrder.getCurrencyToSell(),
                        exchangeOrder.getGUID()))
                .build();
    }
}
