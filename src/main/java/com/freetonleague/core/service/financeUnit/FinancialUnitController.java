package com.freetonleague.core.service.financeUnit;

import com.freetonleague.core.domain.dto.AccountDepositFinUnitDto;
import com.freetonleague.core.domain.enums.BankProviderType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = FinancialUnitController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Financial Management Controller (for financial unit")
public class FinancialUnitController {

    public static final String BASE_PATH = "/api/financial";
    public static final String PATH_DEPOSIT = "/deposit/";

    private final RestFinancialUnitFacade restFinancialUnitFacade;

    @ApiOperation("Callback method for deposit to user accounts")
    @PostMapping(path = PATH_DEPOSIT)
    public ResponseEntity<Void> getDisciplineById(@RequestParam(value = "access_token", required = true) String token,
                                                  @RequestBody AccountDepositFinUnitDto accountDepositInfo) {
        restFinancialUnitFacade.processDeposit(token, accountDepositInfo, BankProviderType.BROXUS);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}