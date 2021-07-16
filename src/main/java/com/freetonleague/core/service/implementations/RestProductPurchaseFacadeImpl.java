package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.product.ProductPurchaseDto;
import com.freetonleague.core.domain.enums.PurchaseStateType;
import com.freetonleague.core.domain.model.product.Product;
import com.freetonleague.core.domain.model.product.ProductPurchase;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.ProductManageException;
import com.freetonleague.core.exception.TeamManageException;
import com.freetonleague.core.exception.config.ExceptionMessages;
import com.freetonleague.core.mapper.ProductPurchaseMapper;
import com.freetonleague.core.security.permissions.CanManageProduct;
import com.freetonleague.core.service.*;
import com.freetonleague.core.util.ProductPropertyConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@RequiredArgsConstructor
@Service
public class RestProductPurchaseFacadeImpl implements RestProductPurchaseFacade {

    private final RestProductFacade restProductFacade;
    private final ProductService productService;
    private final RestUserFacade restUserFacade;
    private final ProductPurchaseService productPurchaseService;
    private final ProductPurchaseMapper productPurchaseMapper;
    private final Validator validator;

    /**
     * Get purchase by id
     */
    @Override
    public ProductPurchaseDto getPurchaseById(long purchaseId) {
        return productPurchaseMapper.toDto(productPurchaseService.getPurchaseById(purchaseId));
    }

    /**
     * Get purchase list with filtering parameters (product, user.leagueId)
     */
    @Override
    public Page<ProductPurchaseDto> getPurchaseList(Pageable pageable, String leagueId, Long productId,
                                                    List<PurchaseStateType> statusList) {
        Product product = nonNull(productId) ? restProductFacade.getVerifiedProductById(productId) : null;
        User user = isBlank(leagueId) ? null : restUserFacade.getVerifiedUserByLeagueId(leagueId);
        return productPurchaseService.getPurchaseListForProduct(pageable, user, product, statusList)
                .map(productPurchaseMapper::toDto);
    }

    /**
     * Registry new product purchase
     */
    @Override
    public ProductPurchaseDto createPurchase(ProductPurchaseDto productPurchaseDto, User currentUser) {
        productPurchaseDto.setId(null);
        productPurchaseDto.setState(PurchaseStateType.APPROVE);
        productPurchaseDto.setManagerComment(null);
        ProductPurchase newProductPurchase = this.getVerifiedProductPurchaseByDto(productPurchaseDto);

        if (!newProductPurchase.getUser().equals(currentUser)) {
            log.warn("~ forbiddenException for create purchase to product.id '{}' for user.leagueId '{}' from user '{}'.",
                    productPurchaseDto.getProductId(), productPurchaseDto.getLeagueId(), currentUser);
            throw new ProductManageException(ExceptionMessages.PRODUCT_PURCHASE_CREATION_ERROR,
                    "User can buy product by himself. Session user not equals specified leagueId.");
        }

        Product product = newProductPurchase.getProduct();
        //check status of product
        if (!product.getStatus().isActive()) {
            log.warn("~ forbiddenException for create new purchase for user '{}' to product.id '{}' with status '{}'. " +
                            "Product is closed for new purchases",
                    productPurchaseDto.getLeagueId(), product.getId(), product.getStatus());
            throw new ProductManageException(ExceptionMessages.PRODUCT_PURCHASE_VERIFICATION_ERROR,
                    String.format("Product '%s' is not active and closed for new purchases. Request rejected.",
                            product.getId()));
        }

        //check if product has quantity limit and it's in stock
        if (product.hasQuantityLimit()) {
            Double updatedQuantityInStock = productService.getProductQuantity(product);
            Double productPurchaseCount = productPurchaseDto.getPurchaseQuantity();
            log.debug("^ try to verify purchase of product.id '{}', quantity in stock  '{}', requirement quantity '{}'," +
                            " we have requirement quantity - '{}'", product.getId(), updatedQuantityInStock,
                    productPurchaseCount, updatedQuantityInStock >= productPurchaseCount);
            if (updatedQuantityInStock < productPurchaseCount) {
                log.warn("~ forbiddenException for create new purchase for user '{}' to product.id '{}'. " +
                                "Required quantity '{}' of product is out of stock '{}'. Purchase is rejected",
                        productPurchaseDto.getLeagueId(), product.getId(), productPurchaseCount, updatedQuantityInStock);
                throw new ProductManageException(ExceptionMessages.PRODUCT_IS_OUT_OF_STOCK_ERROR,
                        String.format("Required quantity '%s' of product is out of stock '%s'. Request rejected.",
                                productPurchaseCount, updatedQuantityInStock));
            }
        }

        // TODO validate selectedProductParameters
//        if (product.hasTextLabel() && !newProductPurchase.hasTextLabelAnswer()) {
//            log.warn("~ parameter 'textLabelAnswer' is not set for purchase to createPurchaseToProduct");
//            throw new ValidationException(ExceptionMessages.PRODUCT_PURCHASE_VALIDATION_ERROR, "textLabelAnswer",
//                    "parameter textLabelAnswer is required for createPurchaseToProduct");
//        }

        //save purchase
        newProductPurchase = productPurchaseService.addPurchase(newProductPurchase);
        if (isNull(newProductPurchase)) {
            log.error("!> error while creating user purchase to product by user.id '{}' to product.id '{}'.",
                    productPurchaseDto.getProductId(), productPurchaseDto.getProductId());
            throw new ProductManageException(ExceptionMessages.PRODUCT_PURCHASE_CREATION_ERROR,
                    "User purchase was not saved on Portal. Check requested params.");
        }
        return productPurchaseMapper.toDto(newProductPurchase);
    }

    /**
     * Edit product purchase (only state)
     */
    @CanManageProduct
    @Override
    public ProductPurchaseDto editPurchase(Long purchaseId, PurchaseStateType currentProductPurchaseState, String managerComment, User currentUser) {
        ProductPurchase userPurchase = this.getVerifiedProductPurchaseById(purchaseId);

        if (isNull(userPurchase)) {
            log.debug("^ User purchase to product with requested parameters userPurchaseId '{}' was not found. " +
                    "'editPurchaseToProduct' in RestProductPurchaseFacade request denied", purchaseId);
            throw new TeamManageException(ExceptionMessages.PRODUCT_PURCHASE_NOT_FOUND_ERROR, "User purchase to product with requested id " + purchaseId + " was not found");
        }

        userPurchase.setState(currentProductPurchaseState);
        userPurchase.setManagerComment(managerComment);
        ProductPurchase savedProductPurchase = productPurchaseService.editPurchase(userPurchase);
        if (isNull(savedProductPurchase)) {
            log.error("!> error while modifying user purchase to product '{}'.", userPurchase);
            throw new ProductManageException(ExceptionMessages.PRODUCT_PURCHASE_MODIFICATION_ERROR,
                    "User purchase to product was not saved on Portal. Check requested params.");
        }
        return productPurchaseMapper.toDto(savedProductPurchase);
    }

    /**
     * Returns product purchase by id with privacy check
     */
    @Override
    public ProductPurchase getVerifiedProductPurchaseById(long id) {
        ProductPurchase productProductPurchase = productPurchaseService.getPurchaseById(id);
        if (isNull(productProductPurchase)) {
            log.debug("^ User purchase to product with requested id '{}' was not found. " +
                    "'getVerifiedProductPurchaseById' in RestTournamentTeamFacadeImpl request denied", id);
            throw new ProductManageException(ExceptionMessages.PRODUCT_PURCHASE_NOT_FOUND_ERROR,
                    "Product purchase with requested id " + id + " was not found");
        }
        return productProductPurchase;
    }

    /**
     * Getting product by DTO with deep validation and privacy check
     */
    private ProductPurchase getVerifiedProductPurchaseByDto(ProductPurchaseDto productPurchaseDto) {
        // Verify Product information
        Set<ConstraintViolation<ProductPurchaseDto>> violations = validator.validate(productPurchaseDto);
        if (!violations.isEmpty()) {
            log.debug("^ transmitted ProductPurchaseDto: '{}' have constraint violations: '{}'", productPurchaseDto, violations);
            throw new ConstraintViolationException(violations);
        }
        ProductPurchase productPurchase = productPurchaseMapper.fromDto(productPurchaseDto);
        productPurchase.setSelectedProductParameters(ProductPropertyConverter.
                convertAndValidateSelectedProperties(productPurchase.getSelectedProductParameters()));
        User user = restUserFacade.getVerifiedUserByLeagueId(productPurchaseDto.getLeagueId());
        Product product = restProductFacade.getVerifiedProductById(productPurchaseDto.getProductId());
        productPurchase.setProduct(product);
        productPurchase.setUser(user);
        return productPurchase;
    }
}
