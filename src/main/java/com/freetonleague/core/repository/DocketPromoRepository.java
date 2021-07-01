package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.DocketPromoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocketPromoRepository extends JpaRepository<DocketPromoEntity, Long> {

    Optional<DocketPromoEntity> findByPromoCode(String promoCode);

    boolean existsByPromoCode(String promoCode);

}
