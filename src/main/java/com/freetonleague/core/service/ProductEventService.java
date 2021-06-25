package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.AccountTransactionInfoDto;
import com.freetonleague.core.domain.enums.ProductStatusType;
import com.freetonleague.core.domain.enums.PurchaseStateType;
import com.freetonleague.core.domain.model.Product;
import com.freetonleague.core.domain.model.ProductPurchase;

import java.util.List;


public interface ProductEventService {

    /**
     * Process product status changing
     */
    void processProductStatusChange(Product docket, ProductStatusType newProductStatusType);

    /**
     * Process product purchase payment
     */
    List<AccountTransactionInfoDto> processProductPurchasePayment(ProductPurchase docketUserProposal);

    /**
     * Process product purchase status changing
     */
    void processProductPurchaseStateChange(ProductPurchase productPurchase, PurchaseStateType state);
}
