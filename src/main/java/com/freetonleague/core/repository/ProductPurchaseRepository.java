package com.freetonleague.core.repository;

import com.freetonleague.core.domain.enums.product.ProductPurchaseStateType;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.domain.model.product.Product;
import com.freetonleague.core.domain.model.product.ProductPurchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.web.PageableDefault;

import java.util.List;

public interface ProductPurchaseRepository extends JpaRepository<ProductPurchase, Long>,
        JpaSpecificationExecutor<ProductPurchase> {

    Page<ProductPurchase> findAllByProductAndUserAndStateIn(@PageableDefault Pageable pageable, Product product,
                                                            User user, List<ProductPurchaseStateType> state);

    Page<ProductPurchase> findAllByProductAndStateIn(@PageableDefault Pageable pageable,
                                                     Product product, List<ProductPurchaseStateType> state);

    Page<ProductPurchase> findAllByUserAndStateIn(@PageableDefault Pageable pageable, User user,
                                                  List<ProductPurchaseStateType> state);

    Page<ProductPurchase> findAllByStateIn(@PageableDefault Pageable pageable, List<ProductPurchaseStateType> state);
}
