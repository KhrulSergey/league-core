package com.freetonleague.core.controller;

import com.freetonleague.core.controller.api.DocketPromoApi;
import com.freetonleague.core.domain.filter.DocketPromoCreationFilter;
import com.freetonleague.core.domain.model.DocketPromoEntity;
import com.freetonleague.core.service.DocketPromoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DocketPromoController implements DocketPromoApi {

    private final DocketPromoService docketPromoService;

    @Override
    public DocketPromoEntity getById(Long id) {
        return docketPromoService.getById(id);
    }

    @Override
    public List<DocketPromoEntity> getAll() {
        return docketPromoService.getAll();
    }

    @Override
    public DocketPromoEntity create(DocketPromoCreationFilter filter) {
        return docketPromoService.createByFilter(filter);
    }

}
