package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.finance.AccountTransactionInfoDto;
import com.freetonleague.core.domain.enums.product.ProductPurchaseStateType;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.domain.model.product.Product;
import com.freetonleague.core.domain.model.product.ProductPurchase;
import com.freetonleague.core.repository.ProductPurchaseRepository;
import com.freetonleague.core.service.ProductEventService;
import com.freetonleague.core.service.ProductPurchaseService;
import com.freetonleague.core.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Transactional
@Slf4j
@RequiredArgsConstructor
@Service
public class ProductPurchaseServiceImpl implements ProductPurchaseService {

    private final ProductPurchaseRepository productPurchaseRepository;

    @Lazy
    @Autowired
    private ProductService productService;

    @Lazy
    @Autowired
    private ProductEventService productEventService;

    /**
     * Returns product purchase by id.
     */
    @Override

    public ProductPurchase getPurchaseById(long id) {
        log.debug("^ trying to get product purchase by id '{}'", id);
        return productPurchaseRepository.findById(id).orElse(null);
    }

    /**
     * Returns list of all product purchase filtered by requested params
     */
    @Override
    public Page<ProductPurchase> getPurchaseListForProduct(Pageable pageable, User user, Product product, List<ProductPurchaseStateType> statusList) {
        if (isNull(pageable)) {
            log.error("!> requesting getProposalListForProduct for NULL pageable. Check evoking clients");
            return null;
        }
        List<ProductPurchaseStateType> filteredProposalStateList = List.of(ProductPurchaseStateType.values());
        boolean filterByProductEnabled = nonNull(product);
        boolean filterByUserEnabled = nonNull(user);
        log.debug("^ trying to get product purchase list with pageable params: '{}', filterByProductEnabled '{}', filterByUserEnabled '{}', statusList '{}'",
                pageable, filterByProductEnabled, filterByUserEnabled, filteredProposalStateList);
        Page<ProductPurchase> productPurchaseList;
        if (filterByProductEnabled && filterByUserEnabled) {
            productPurchaseList = productPurchaseRepository.findAllByProductAndUserAndStateIn(pageable, product, user, filteredProposalStateList);
        } else if (filterByUserEnabled) {
            productPurchaseList = productPurchaseRepository.findAllByUserAndStateIn(pageable, user, filteredProposalStateList);
        } else if (filterByProductEnabled) {
            productPurchaseList = productPurchaseRepository.findAllByProductAndStateIn(pageable, product, filteredProposalStateList);
        } else {
            productPurchaseList = productPurchaseRepository.findAllByStateIn(pageable, filteredProposalStateList);
        }
        return productPurchaseList;
    }

    /**
     * Returns saved new product purchase.
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ProductPurchase addPurchase(ProductPurchase productPurchase) {
        if (isNull(productPurchase) || isNull(productPurchase.getProduct())) {
            log.error("!> requesting addProposal for NULL productPurchase '{}' or NULL productPurchase.product. Check evoking clients",
                    productPurchase);
            return null;
        }
        Product product = productPurchase.getProduct();
        if (product.hasQuantityLimit() && productService.getProductQuantity(product) < productPurchase.getPurchaseQuantity()) {
            log.error("!> requesting addPurchase for product with quantity stock limit exceeded. Check evoking clients");
            return null;
        }
        log.debug("^ trying to add new product purchase '{}'", productPurchase);
        if (product.getAccessType().isPaid()) {
            // calculate purchase amount
            productPurchase.setPurchaseTotalAmount(this.calculatePurchaseAmount(productPurchase));
            // make payment from buyer to service account
            List<AccountTransactionInfoDto> paymentList = productEventService.processProductPurchasePayment(productPurchase);
            productPurchase.setPurchasePaymentList(paymentList);
        } else {
            productPurchase.setPurchaseTotalAmount(0.0);
        }
        if (product.hasQuantityLimit()) {
            double newQuantityInStock = product.getQuantityInStock() - productPurchase.getPurchaseQuantity();
            if (newQuantityInStock < 0) {
                log.error("!> requesting change product quantity in stock to less then ZERO by new purchase. Request is prohibited. Check evoking clients");
                return null;
            }
            product.setQuantityInStock(product.getQuantityInStock() - productPurchase.getPurchaseQuantity());
            productService.editProduct(product);
        }
        productPurchaseRepository.save(productPurchase);
        this.handleProductPurchaseStateChanged(productPurchase);
        return productPurchase;
    }

    /**
     * Edit product purchase in DB.
     */
    @Override
    public ProductPurchase editPurchase(ProductPurchase productPurchase) {
        if (isNull(productPurchase)) {
            log.error("!> requesting modify product purchase with editProposal for NULL productPurchase. Check evoking clients");
            return null;
        }
        if (!isExistsProductPurchaseById(productPurchase.getId())) {
            log.error("!> requesting modify product purchase or non-existed proposal.id '{}'. Check evoking clients",
                    productPurchase.getId());
            return null;
        }
        log.debug("^ trying to modify product purchase '{}'", productPurchase);
        if (productPurchase.isStateChanged()) {
            this.handleProductPurchaseStateChanged(productPurchase);
        }
        return productPurchaseRepository.save(productPurchase);
    }

    /**
     * Returns calculated purchase amount for specified product purchase
     */
    @Override
    public Double calculatePurchaseAmount(ProductPurchase productPurchase) {
        if (isNull(productPurchase)) {
            log.error("!> requesting calculatePurchaseAmount for NULL productPurchase. Check evoking clients");
            return null;
        }
        Product product = productPurchase.getProduct();
        if (isNull(product)) {
            log.error("!> requesting calculatePurchaseAmount for NULL product. Check evoking clients");
            return null;
        }
        // if product is free, then purchase amount is 0
        if (product.getAccessType().isFree()) {
            return 0.0;
        }
        Double purchaseQuantity = productPurchase.getPurchaseQuantity();
        if (isNull(purchaseQuantity)) {
            log.error("!> requesting calculatePurchaseAmount for NULL purchaseQuantity. Check evoking clients");
            return null;
        }
        Double productCost = product.getCost();
        if (isNull(productCost)) {
            log.error("!> requesting calculatePurchaseAmount for NULL productCost. Check evoking clients");
            return null;
        }
        Double purchaseAmount = purchaseQuantity * productCost;
        log.debug("^ trying to calculatePurchaseAmount for product.id {}, purchase quantity '{}', total amount '{}'",
                product.getId(), productCost, purchaseAmount);
        return purchaseAmount;
    }

    private boolean isExistsProductPurchaseById(long id) {
        return productPurchaseRepository.existsById(id);
    }

    /**
     * Prototype for handle product purchase state
     */
    private void handleProductPurchaseStateChanged(ProductPurchase productPurchase) {
        log.warn("~ status for product purchase with id '{}' was changed from '{}' to '{}' ",
                productPurchase.getId(), productPurchase.getPrevState(), productPurchase.getState());
        productEventService.processProductPurchaseStateChange(productPurchase, productPurchase.getState());
        productPurchase.setPrevState(productPurchase.getState());
    }
}
