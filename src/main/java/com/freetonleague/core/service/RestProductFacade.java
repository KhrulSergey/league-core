package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.product.ProductDto;
import com.freetonleague.core.domain.enums.ProductStatusType;
import com.freetonleague.core.domain.model.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service-facade for managing products
 */
public interface RestProductFacade {

    /**
     * Returns founded product by id
     *
     * @param id of product to search
     * @return product entity
     */
    ProductDto getProduct(long id);

    /**
     * Returns list of all teams filtered by requested params with detailed info
     *
     * @param pageable        filtered params to search product
     * @param creatorLeagueId filter params
     * @param statusList      filter params
     * @return list of team entities
     */
    Page<ProductDto> getProductList(Pageable pageable, String creatorLeagueId, List<ProductStatusType> statusList);

    /**
     * Add new product to DB.
     *
     * @param productDto to be added
     * @return Added product
     */
    ProductDto addProduct(ProductDto productDto);

    /**
     * Edit product in DB.
     *
     * @param productDto to be edited
     * @return Edited product
     */
    ProductDto editProduct(ProductDto productDto);

    /**
     * Delete product in DB.
     *
     * @param id of product to search
     * @return deleted product
     */
    ProductDto deleteProduct(long id);

    /**
     * Getting product by id with privacy check
     */
    Product getVerifiedProductById(long id);
}
