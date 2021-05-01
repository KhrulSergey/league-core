package com.freetonleague.core.service.financeUnit;

import com.freetonleague.core.domain.dto.AccountDepositFinUnitDto;
import com.freetonleague.core.domain.enums.BankProviderType;
import com.freetonleague.core.domain.model.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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
                                                  @RequestBody AccountDepositFinUnitDto accountDepositInfo,
                                                  @ApiIgnore @AuthenticationPrincipal User user) {
        restFinancialUnitFacade.processDeposit(accountDepositInfo, BankProviderType.BROXUS, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
