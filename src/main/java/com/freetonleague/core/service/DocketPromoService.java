package com.freetonleague.core.service;

import com.freetonleague.core.domain.filter.DocketPromoCreationFilter;
import com.freetonleague.core.domain.model.DocketPromoEntity;
import com.freetonleague.core.domain.model.User;

import java.util.List;

public interface DocketPromoService {

    List<DocketPromoEntity> getAll();

    DocketPromoEntity getById(Long id);

    DocketPromoEntity createByFilter(DocketPromoCreationFilter filter);

    void usePromo(String promoCode, User user);

}
