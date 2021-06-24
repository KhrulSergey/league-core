package com.freetonleague.core.common.bean;

import com.freetonleague.core.domain.model.Session;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.repository.SessionRepository;
import com.freetonleague.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.extension.ParameterContext;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthParameterResolverBean {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    private String username;

    public Object resolveParameter(ParameterContext parameterContext) {
        Class<?> type = parameterContext.getParameter().getType();

        User user = username == null ?
                userRepository.findAll().stream().findFirst().get() :
                userRepository.findByUsername(username);

        username = user.getUsername();

        if (User.class.equals(type)) {
            return user;
        }

        if (HttpHeaders.class.equals(type)) {
            return getAuthHeadersForUser(user);
        }

        throw new UnsupportedOperationException();

    }

    private HttpHeaders getAuthHeadersForUser(User user) {
        Session session = sessionRepository.findByUserAndExpirationAfter(user, LocalDateTime.now());

        if (session == null) {
            session = sessionRepository.save(Session.builder()
                    .user(user)
                    .authProvider("CORE")
                    .token(UUID.randomUUID().toString())
                    .expiration(LocalDateTime.now().plusHours(1))
                    .build());
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", session.getToken());

        return httpHeaders;
    }

}
