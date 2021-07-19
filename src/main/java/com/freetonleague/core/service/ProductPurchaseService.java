package com.freetonleague.core.service;


import com.freetonleague.core.domain.enums.PurchaseStateType;
import com.freetonleague.core.domain.model.product.Product;
import com.freetonleague.core.domain.model.product.ProductPurchase;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductPurchaseService {

    /**
     * Returns product purchase by id.
     *
     * @param id of product purchase to search
     * @return user product purchase entity
     */
    ProductPurchase getPurchaseById(long id);

    /**
     * Returns list of all product purchase filtered by requested params
     *
     * @param pageable filtered params to search product purchases
     * @param product  params to search product purchases
     * @return list of product purchases
     */
    Page<ProductPurchase> getPurchaseListForProduct(Pageable pageable, User user, Product product, List<PurchaseStateType> statusList);

    /**
     * Returns saved new product purchase.
     *
     * @param userPurchase data to be saved id DB
     * @return new product purchase
     */
    ProductPurchase addPurchase(ProductPurchase userPurchase);

    /**
     * Edit product purchase in DB.
     *
     * @param userPurchase to be edited
     * @return Edited product purchase
     */
    ProductPurchase editPurchase(ProductPurchase userPurchase);

    /**
     * Returns calculated purchase amount for specified product purchase
     */
    Double calculatePurchaseAmount(ProductPurchase userPurchase);
}
