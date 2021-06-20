package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.ProductDto;
import com.freetonleague.core.domain.enums.ProductStatusType;
import com.freetonleague.core.domain.model.Product;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.ProductManageException;
import com.freetonleague.core.exception.ValidationException;
import com.freetonleague.core.exception.config.ExceptionMessages;
import com.freetonleague.core.mapper.ProductMapper;
import com.freetonleague.core.security.permissions.CanManageProduct;
import com.freetonleague.core.service.ProductService;
import com.freetonleague.core.service.RestProductFacade;
import com.freetonleague.core.service.RestUserFacade;
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
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@RequiredArgsConstructor
@Service
public class RestProductFacadeImpl implements RestProductFacade {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final RestUserFacade restUserFacade;
    private final Validator validator;

    /**
     * Returns founded product by id
     */
    @Override
    public ProductDto getProduct(long id) {
        return productMapper.toDto(this.getVerifiedProductById(id));
    }

    /**
     * Returns list of all product filtered by requested params with detailed info
     */
    @Override
    public Page<ProductDto> getProductList(Pageable pageable, String creatorLeagueId, List<ProductStatusType> statusList) {
        User creatorUser = null;
        if (!isBlank(creatorLeagueId)) {
            creatorUser = restUserFacade.getVerifiedUserByLeagueId(creatorLeagueId);
        }
        return productService.getProductList(pageable, creatorUser, statusList).map(productMapper::toDto);
    }

    /**
     * Add new product to DB.
     */
    @CanManageProduct
    @Override
    public ProductDto addProduct(ProductDto productDto) {
        productDto.setId(null);
        productDto.setStatus(ProductStatusType.ACTIVE);

        Product product = this.getVerifiedProductByDto(productDto);
        product = productService.addProduct(product);

        if (isNull(product)) {
            log.error("!> error while creating product from dto '{}'.", productDto);
            throw new ProductManageException(ExceptionMessages.PRODUCT_CREATION_ERROR,
                    "Product was not saved on Portal. Check requested params.");
        }
        return productMapper.toDto(product);
    }

    /**
     * Edit product in DB.
     */
    @CanManageProduct
    @Override
    public ProductDto editProduct(ProductDto productDto) {
        if (isNull(productDto.getId())) {
            log.warn("~ parameter 'product.id' is not set for editProduct");
            throw new ValidationException(ExceptionMessages.PRODUCT_VALIDATION_ERROR, "product id",
                    "parameter 'product id' is not set for editProduct");
        }

        if (productDto.getStatus().isDeleted()) {
            log.warn("~ product deleting was declined in editProduct. This operation should be done with specific method.");
            throw new ProductManageException(ExceptionMessages.PRODUCT_STATUS_DELETE_ERROR,
                    "Modifying product was rejected. Check requested params and method.");
        }

        Product modifiedProduct = productService.editProduct(this.getVerifiedProductByDto(productDto));
        if (isNull(modifiedProduct)) {
            log.error("!> error while modifying product from dto '{}'.", productDto);
            throw new ProductManageException(ExceptionMessages.PRODUCT_MODIFICATION_ERROR,
                    "Product was not updated on Portal. Check requested params.");
        }
        return productMapper.toDto(modifiedProduct);
    }

    /**
     * Delete product in DB.
     */
    @CanManageProduct
    @Override
    public ProductDto deleteProduct(long id) {
        Product product = this.getVerifiedProductById(id);
        product = productService.deleteProduct(product);

        if (isNull(product)) {
            log.error("!> error while deleting product with id '{}'.", id);
            throw new ProductManageException(ExceptionMessages.PRODUCT_MODIFICATION_ERROR,
                    "Product was not deleted on Portal. Check requested params.");
        }
        return productMapper.toDto(product);
    }

    /**
     * Getting product by id with privacy check
     */
    @Override
    public Product getVerifiedProductById(long id) {
        Product product = productService.getProduct(id);
        if (isNull(product)) {
            log.debug("^ Product with requested id '{}' was not found. 'getVerifiedProductById' in RestProductFacadeImpl request denied", id);
            throw new ProductManageException(ExceptionMessages.PRODUCT_NOT_FOUND_ERROR, "Product with requested id " + id + " was not found");
        }
        if (product.getStatus().isDeleted()) {
            log.debug("^ Product with requested id '{}' was '{}'. 'getVerifiedProductById' in RestProductFacadeImpl request denied", id, product.getStatus());
            throw new ProductManageException(ExceptionMessages.PRODUCT_VISIBLE_ERROR, "Visible product with requested id " + id + " was not found");
        }
        return product;
    }

    /**
     * Getting product by DTO with deep validation and privacy check
     */
    private Product getVerifiedProductByDto(ProductDto productDto) {
        // Verify Product information
        Set<ConstraintViolation<ProductDto>> violations = validator.validate(productDto);
        if (!violations.isEmpty()) {
            log.debug("^ transmitted ProductDto: '{}' have constraint violations: '{}'", productDto, violations);
            throw new ConstraintViolationException(violations);
        }
        return productMapper.fromDto(productDto);
    }
}
