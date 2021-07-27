package com.freetonleague.core.controller;

import com.freetonleague.core.domain.dto.finance.ExchangeOrderDto;
import com.freetonleague.core.domain.dto.finance.ExchangeRatioDto;
import com.freetonleague.core.domain.enums.finance.Currency;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.RestFinanceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


@RestController
@RequestMapping(path = FinancialExchangeController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Financial Accounts Data Controller")
public class FinancialExchangeController {

    public static final String BASE_PATH = "/api/accounts";
    public static final String PATH_GET_EXCHANGE_RATE = "/transaction/exchange-rate";
    public static final String PATH_CREATE_EXCHANGE = "/transaction/exchange";
    public static final String PATH_APPROVE_EXCHANGE = "/transaction/exchange-approve";

    private final RestFinanceFacade restFinanceFacade;

    @ApiOperation("Get exchange rate for specified currencies")
    @GetMapping(path = PATH_GET_EXCHANGE_RATE)
    public ResponseEntity<ExchangeRatioDto> getExchangeRateForCurrencies(@RequestParam(value = "currency_to_buy", defaultValue = "TON") Currency currencyToBuy,
                                                                         @RequestParam(value = "currency_to_sell", defaultValue = "RUB") Currency currencyToSell) {
        return new ResponseEntity<>(restFinanceFacade.getExchangeRateForCurrencies(currencyToBuy, currencyToSell), HttpStatus.OK);
    }

    @ApiOperation("Create exchange order for specified currencies and account GUID (or from user session)")
    @PostMapping(path = PATH_CREATE_EXCHANGE)
    public ResponseEntity<ExchangeOrderDto> createExchangeTransaction(@RequestParam(value = "amount-to-buy") Double amountToBuy,
                                                                      @RequestParam(value = "currency_to_buy", required = false) Currency currencyToBuy,
                                                                      @RequestParam(value = "currency_to_sell") Currency currencyToSell,
                                                                      @RequestParam(value = "target_account_guid", required = false) String targetAccountGUID,
                                                                      @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restFinanceFacade.createExchangeOrder(amountToBuy, currencyToBuy, currencyToSell, targetAccountGUID, user), HttpStatus.CREATED);
    }

    @ApiOperation("Approve exchange order for specified order GUID (only for admin)")
    @PostMapping(path = PATH_APPROVE_EXCHANGE)
    public ResponseEntity<ExchangeOrderDto> approveExchangeTransaction(@RequestParam(value = "order-guid") String exchangeOrderGuid,
                                                                       @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restFinanceFacade.approveExchangeOrder(exchangeOrderGuid, user), HttpStatus.OK);
    }
}
