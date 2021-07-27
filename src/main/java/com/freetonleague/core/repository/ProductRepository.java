package com.freetonleague.core.repository;

import com.freetonleague.core.domain.enums.product.ProductStatusType;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.domain.model.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

    /**
     * Returns all dockets with status in the list and pageable params
     */
    Page<Product> findAllByStatusIn(Pageable pageable, List<ProductStatusType> statusList);

    /**
     * Returns all products with status in the list, created by specified user and pageable params
     */
    Page<Product> findAllByStatusInAndCreatedBy(Pageable pageable, List<ProductStatusType> statusList, User user);

    /**
     * Returns all products with created by specified user
     */
    Page<Product> findAllByCreatedBy(Pageable pageable, User user);

    @Query(value = "select p.quantityInStock from Product p where p.id = :id")
    Double getProductQuantity(@Param("id") Long id);
}
