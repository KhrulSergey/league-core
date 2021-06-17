package com.freetonleague.core;

import com.freetonleague.core.common.IntegrationTest;
import com.freetonleague.core.controller.UserController;
import com.freetonleague.core.domain.enums.UserParameterType;
import com.freetonleague.core.domain.filter.UserInfoFilter;
import com.freetonleague.core.domain.model.Session;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.repository.SessionRepository;
import com.freetonleague.core.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

//TODO: rename me
public class TestTest extends IntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void test() {
        User user = userRepository.findAll().stream().findFirst().get();

        sessionRepository.save(Session.builder()
                .user(user)
                .authProvider("CORE")
                .token(UUID.randomUUID().toString())
                .expiration(LocalDateTime.now().plusHours(1))
                .build());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", sessionRepository.findAll().stream()
                .findFirst().get().getToken()
        );

        UserInfoFilter filter = new UserInfoFilter();
        filter.setParameters(Map.of(UserParameterType.PUBG_ID, "im id xD"));

        HttpEntity<UserInfoFilter> httpEntity = new HttpEntity<>(filter, httpHeaders);

        ResponseEntity<String> responseEntity = testRestTemplate.exchange(
                UserController.BASE_PATH, HttpMethod.PUT, httpEntity, String.class
        );

        Assertions.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());

        User userAfterParametersUpdate = userRepository.findByUsername(user.getUsername());

        Assertions.assertEquals(userAfterParametersUpdate.getParameters().size(), 1);
    }

}
