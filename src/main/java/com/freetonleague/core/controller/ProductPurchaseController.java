package com.freetonleague.core.controller;

import com.freetonleague.core.config.ApiPageable;
import com.freetonleague.core.domain.dto.ProductPurchaseDto;
import com.freetonleague.core.domain.enums.PurchaseStateType;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.RestProductPurchaseFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

import static java.util.Objects.nonNull;

@RestController
@RequestMapping(path = ProductPurchaseController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Product purchase Management Controller")
public class ProductPurchaseController {

    public static final String BASE_PATH = "/api/product/purchase";

    public static final String PATH_BUY_PRODUCT = "/";
    public static final String PATH_GET_PRODUCT_PURCHASE = "/";
    public static final String PATH_EDIT_PRODUCT_PURCHASE = "/";
    public static final String PATH_GET_PURCHASE_LIST_BY_PRODUCT = "/list";


    private final RestProductPurchaseFacade productPurchaseFacade;


    @ApiOperation("Get product purchase by id")
    @GetMapping(path = PATH_GET_PRODUCT_PURCHASE)
    public ResponseEntity<ProductPurchaseDto> getProductPurchaseById(@RequestParam(value = "purchase_id") long purchaseId) {
        return new ResponseEntity<>(productPurchaseFacade.getPurchaseById(purchaseId), HttpStatus.OK);
    }

    @ApiPageable
    @ApiOperation("Get product purchase list with parameters")
    @GetMapping(path = PATH_GET_PURCHASE_LIST_BY_PRODUCT)
    public ResponseEntity<Page<ProductPurchaseDto>> getProductPurchaseList(@PageableDefault Pageable pageable,
                                                                           @RequestParam(value = "league_id", required = false) String leagueId,
                                                                           @RequestParam(value = "product_id", required = false) Long productId,
                                                                           @RequestParam(value = "statuses", required = false) PurchaseStateType... statuses) {
        List<PurchaseStateType> statusList = nonNull(statuses) ? List.of(statuses) : null;
        return new ResponseEntity<>(productPurchaseFacade.getPurchaseList(pageable, leagueId, productId, statusList), HttpStatus.OK);
    }

    @ApiOperation("Make (create) purchase of product")
    @PostMapping(path = PATH_BUY_PRODUCT)
    public ResponseEntity<ProductPurchaseDto> makePurchase(@RequestBody ProductPurchaseDto productPurchaseDto,
                                                           @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(productPurchaseFacade.createPurchase(productPurchaseDto, user), HttpStatus.OK);
    }

    @ApiOperation("Change product purchase by purchaseId (available edit only state, for orgs)")
    @PutMapping(path = PATH_EDIT_PRODUCT_PURCHASE)
    public ResponseEntity<ProductPurchaseDto> editProductPurchase(@RequestParam(value = "purchase_id") long purchaseId,
                                                                  @RequestParam(value = "purchase_state") PurchaseStateType purchaseState,
                                                                  @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(productPurchaseFacade.editPurchase(purchaseId, purchaseState, user), HttpStatus.OK);
    }
}
