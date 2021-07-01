package com.freetonleague.core.controller.api;

import com.freetonleague.core.domain.filter.DocketPromoCreationFilter;
import com.freetonleague.core.domain.model.DocketPromoEntity;
import com.freetonleague.core.security.permissions.CanManageDocket;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@Api(value = "Docket promo codes api")
public interface DocketPromoApi {

    String GET_BY_ID_PATH = "/api/docket/promo/{id}";
    String GET_ALL_PATH = "/api/docket/promo";
    String POST_CREATE_PATH = "/api/docket/promo";

    @CanManageDocket
    @GetMapping(GET_BY_ID_PATH)
    @ApiOperation("Getting promo by integer id")
    DocketPromoEntity getById(@PathVariable Long id);

    @CanManageDocket
    @GetMapping(GET_ALL_PATH)
    @ApiOperation("Getting all enabled promos")
    List<DocketPromoEntity> getAll();

    @CanManageDocket
    @PostMapping(POST_CREATE_PATH)
    @ApiOperation("Create docket promo")
    DocketPromoEntity create(
            @RequestBody @Valid DocketPromoCreationFilter filter
    );

}
