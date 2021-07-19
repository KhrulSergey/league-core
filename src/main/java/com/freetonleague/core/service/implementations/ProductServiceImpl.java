package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.enums.ProductStatusType;
import com.freetonleague.core.domain.model.product.Product;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.repository.ProductRepository;
import com.freetonleague.core.service.ProductEventService;
import com.freetonleague.core.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Transactional
@Slf4j
@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final Validator validator;

    @Autowired
    private ProductEventService productEventService;


    /**
     * Returns founded product by id
     */
    @Override
    public Product getProduct(long id) {
        log.debug("^ trying to get product by id: '{}'", id);
        return productRepository.findById(id).orElse(null);
    }

    /**
     * Returns list of all products filtered by requested params
     */
    @Override
    public Page<Product> getProductList(Pageable pageable, User creatorUser, List<ProductStatusType> statusList) {
        if (isNull(pageable)) {
            log.error("!> requesting getProductList for NULL pageable. Check evoking clients");
            return null;
        }
        log.debug("^ trying to get product list with pageable params: '{}' and status list '{}'", pageable, statusList);
        boolean filterByStatusEnabled = isNotEmpty(statusList);
        boolean filterByCreatorEnabled = nonNull(creatorUser);

        if (filterByStatusEnabled && filterByCreatorEnabled) {
            return productRepository.findAllByStatusInAndCreatedBy(pageable, statusList, creatorUser);
        } else if (filterByStatusEnabled) {
            return productRepository.findAllByStatusIn(pageable, statusList);
        } else if (filterByCreatorEnabled) {
            return productRepository.findAllByCreatedBy(pageable, creatorUser);
        }
        return productRepository.findAll(pageable);
    }

//    /**
//     * Returns list of all active Products on portal
//     */
//    @Override
//    public Page<Product> getAllActiveProduct(Pageable pageable) {
//        return productRepository.findAllActive(pageable);
//    }

    /**
     * Add new Product to DB.
     */
    @Override
    public Product addProduct(Product product) {
        if (!this.verifyProduct(product)) {
            return null;
        }
        log.debug("^ trying to add new product '{}'", product);
        product = productRepository.save(product);
        productEventService.processProductStatusChange(product, product.getStatus());
        return product;
    }

    /**
     * Edit product in DB.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public Product editProduct(Product product) {
        if (!this.verifyProduct(product)) {
            return null;
        }
        if (!this.isExistsProductById(product.getId())) {
            log.error("!> requesting modify product.id '{}' and name '{}' for non-existed product. Check evoking clients", product.getId(), product.getName());
            return null;
        }
        log.debug("^ trying to modify product '{}'", product);

        if (product.getStatus().isArchived()) {
            product.setArchivedAt(LocalDateTime.now());
        }
        if (product.isStatusChanged()) {
            this.handleProductStatusChanged(product);
        }
        return productRepository.save(product);
    }

    /**
     * Mark 'deleted' product in DB.
     */
    @Override
    public Product deleteProduct(Product product) {
        if (!this.verifyProduct(product)) {
            return null;
        }
        if (!this.isExistsProductById(product.getId())) {
            log.error("!> requesting delete product for non-existed product. Check evoking clients");
            return null;
        }
        log.debug("^ trying to set 'deleted' mark to product '{}'", product);
        product.setStatus(ProductStatusType.DELETED);
        product = productRepository.save(product);
        this.handleProductStatusChanged(product);
        return product;
    }

    /**
     * Returns sign of product existence for specified id.
     */
    @Override
    public boolean isExistsProductById(long id) {
        return productRepository.existsById(id);
    }

    /**
     * Returns product quantity in stock.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public Double getProductQuantity(Product product) {
        return productRepository.getProductQuantity(product.getId());
    }

    /**
     * Validate product parameters and settings to modify
     */
    private boolean verifyProduct(Product product) {
        if (isNull(product)) {
            log.error("!> requesting modify product with verifyProduct for NULL product. Check evoking clients");
            return false;
        }
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        if (!violations.isEmpty()) {
            log.error("!> requesting modify product id '{}' name '{}' with verifyProduct for product with ConstraintViolations. Check evoking clients",
                    product.getId(), product.getName());
            return false;
        }
        return true;
    }

    /**
     * Prototype for handle product status
     */
    private void handleProductStatusChanged(Product product) {
        log.warn("~ status for product id '{}' was changed from '{}' to '{}' ",
                product.getId(), product.getPrevStatus(), product.getStatus());
        product.setPrevStatus(product.getStatus());
    }
}
