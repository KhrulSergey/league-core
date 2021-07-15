package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.product.ProductPurchaseDto;
import com.freetonleague.core.domain.enums.PurchaseStateType;
import com.freetonleague.core.domain.model.product.ProductPurchase;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service-facade for managing product purchase
 */
public interface RestProductPurchaseFacade {


    /**
     * Get purchase by id
     *
     * @param purchaseId identify of purchase
     */
    ProductPurchaseDto getPurchaseById(long purchaseId);

    /**
     * Get purchase list with filtering parameters (product, user.leagueId)
     *
     * @param leagueId  identify of user-buyer
     * @param productId identify of product
     */
    Page<ProductPurchaseDto> getPurchaseList(Pageable pageable, String leagueId, Long productId, List<PurchaseStateType> statusList);

    /**
     * Registry new product purchase
     *
     * @param productPurchaseDto data to be added
     * @param user               current user from session
     * @return Added purchase
     */
    ProductPurchaseDto createPurchase(ProductPurchaseDto productPurchaseDto, User user);

    /**
     * Edit user purchase of product (only state)
     *
     * @param purchaseId           identify of product purchase
     * @param currentPurchaseState new status of purchase
     * @return Modified purchase
     */
    ProductPurchaseDto editPurchase(Long purchaseId, PurchaseStateType currentPurchaseState, String managerComment, User currentUser);

    /**
     * Returns product purchase by id with privacy check
     */
    ProductPurchase getVerifiedProductPurchaseById(long id);
}
