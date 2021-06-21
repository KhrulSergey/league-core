package com.freetonleague.core.service;


import com.freetonleague.core.domain.enums.ProductStatusType;
import com.freetonleague.core.domain.model.Product;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    /**
     * Returns founded product by id
     *
     * @param id of product to search
     * @return product entity
     */
    Product getProduct(long id);

    /**
     * Returns list of all products filtered by requested params
     *
     * @param pageable   filtered params to search product
     * @param statusList filtered params to search product
     * @return list of products entities
     */
    Page<Product> getProductList(Pageable pageable, User creatorUser, List<ProductStatusType> statusList);

    /**
     * Add new Product to DB.
     *
     * @param product to be added
     * @return Added Product
     */
    Product addProduct(Product product);

    /**
     * Edit product in DB.
     *
     * @param product to be edited
     * @return Edited product
     */
    Product editProduct(Product product);

    /**
     * Mark 'deleted' product in DB.
     *
     * @param product to be deleted
     * @return product with updated fields and deleted status
     */
    Product deleteProduct(Product product);

    /**
     * Returns sign of product existence for specified id.
     *
     * @param id for which product will be find
     * @return true if product exists, false - if not
     */
    boolean isExistsProductById(long id);

    /**
     * Returns product quantity in stock.
     *
     * @param product for which quantity will be find
     * @return quantity in stock for product
     */
    Double getProductQuantity(Product product);
}
