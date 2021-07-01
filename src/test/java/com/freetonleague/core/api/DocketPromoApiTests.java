package com.freetonleague.core.api;

import com.freetonleague.core.common.IntegrationTest;
import com.freetonleague.core.common.TestUserRoles;
import com.freetonleague.core.controller.api.DocketPromoApi;
import com.freetonleague.core.domain.enums.UserRoleType;
import com.freetonleague.core.domain.filter.DocketPromoCreationFilter;
import com.freetonleague.core.domain.model.DocketPromoEntity;
import com.freetonleague.core.repository.DocketPromoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public class DocketPromoApiTests extends IntegrationTest {

    @Autowired
    private DocketPromoRepository docketPromoRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @TestUserRoles(UserRoleType.ADMIN)
    public void docketPromoShouldCreated(HttpHeaders httpHeaders) {
        DocketPromoCreationFilter filter = DocketPromoCreationFilter.builder()
                .maxUsages(5)
                .build();

        ResponseEntity<DocketPromoEntity> createResponseEntity = create(filter, httpHeaders);

        Assertions.assertEquals(HttpStatus.OK, createResponseEntity.getStatusCode());
        Assertions.assertNotNull(createResponseEntity.getBody());

        ResponseEntity<DocketPromoEntity> getByIdResponseEntity = getById(
                createResponseEntity.getBody().getId(), httpHeaders);

        Assertions.assertEquals(HttpStatus.OK, getByIdResponseEntity.getStatusCode());
        Assertions.assertNotNull(getByIdResponseEntity.getBody());

    }

    @Test
    @TestUserRoles(UserRoleType.ADMIN)
    public void docketPromoShouldCreationShouldReturn400WithInvalidParams(HttpHeaders httpHeaders) {
        DocketPromoCreationFilter filter = DocketPromoCreationFilter.builder()
                .maxUsages(-1)
                .build();

        ResponseEntity<DocketPromoEntity> responseEntity = create(filter, httpHeaders);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void docketPromoShouldNotCreatedByStandardUser(HttpHeaders httpHeaders) {
        DocketPromoCreationFilter filter = DocketPromoCreationFilter.builder()
                .maxUsages(5)
                .build();

        ResponseEntity<DocketPromoEntity> responseEntity = create(filter, httpHeaders);

        Assertions.assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    private ResponseEntity<DocketPromoEntity> getById(Long id, HttpHeaders httpHeaders) {
        HttpEntity<Void> httpEntity = new HttpEntity<>(
                null,
                httpHeaders
        );

        return testRestTemplate.exchange(
                DocketPromoApi.GET_BY_ID_PATH,
                HttpMethod.GET,
                httpEntity,
                DocketPromoEntity.class,
                Map.of("id", id)
        );
    }

    private ResponseEntity<List<DocketPromoEntity>> getAll(HttpHeaders httpHeaders) {
        return testRestTemplate.exchange(
                DocketPromoApi.GET_ALL_PATH,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                }
        );

    }

    private ResponseEntity<DocketPromoEntity> create(DocketPromoCreationFilter filter, HttpHeaders httpHeaders) {
        HttpEntity<DocketPromoCreationFilter> httpEntity = new HttpEntity<>(
                filter,
                httpHeaders
        );

        return testRestTemplate.exchange(
                DocketPromoApi.POST_CREATE_PATH,
                HttpMethod.POST,
                httpEntity,
                DocketPromoEntity.class
        );
    }

}
