package com.freetonleague.core.service.financeUnit;

import com.freetonleague.core.domain.dto.finance.AccountDepositFinUnitDto;
import com.freetonleague.core.domain.enums.finance.BankProviderType;
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

    /**
     * The same value as from "${freetonleague.session.service-token-name}"
     */
    private final String staticServiceTokenName = "access_token";

    @ApiOperation("Callback method for deposit to user accounts")
    @PostMapping(path = PATH_DEPOSIT)
    public ResponseEntity<Void> processDepositOperation(@RequestParam(value = staticServiceTokenName, required = false) String token,
                                                        @RequestBody AccountDepositFinUnitDto accountDepositInfo) {
        restFinancialUnitFacade.processDeposit(token, accountDepositInfo, BankProviderType.BROXUS);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
