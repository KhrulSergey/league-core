package com.freetonleague.core.common.bean;

import com.freetonleague.core.common.TestUserRoles;
import com.freetonleague.core.domain.enums.UserRoleType;
import com.freetonleague.core.domain.enums.UserStatusType;
import com.freetonleague.core.domain.model.Role;
import com.freetonleague.core.domain.model.Session;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.repository.RoleRepository;
import com.freetonleague.core.repository.SessionRepository;
import com.freetonleague.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthParameterResolverBean {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final RoleRepository roleRepository;

    private String username;

    private final String USERNAME_STARTS_WITH = "TEST_";

    @Transactional
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Class<?> type = parameterContext.getParameter().getType();

        Method requiredTestMethod = extensionContext.getRequiredTestMethod();

        TestUserRoles testUserRolesAnnotation = AnnotatedElementUtils.getMergedAnnotation(
                requiredTestMethod, TestUserRoles.class);

        List<UserRoleType> userRoleTypes = testUserRolesAnnotation != null ?
                Arrays.asList(testUserRolesAnnotation.value()) :
                List.of(UserRoleType.REGULAR);

        User user = resolveUserForRoles(userRoleTypes);

        if (User.class.equals(type)) {
            return user;
        }

        if (HttpHeaders.class.equals(type)) {
            return getAuthHeadersForUser(user);
        }

        throw new UnsupportedOperationException();

    }

    private User resolveUserForRoles(List<UserRoleType> userRoleTypes) {
        User userForRoles = getUserForRoles(userRoleTypes);

        if (userForRoles == null) {

            userForRoles = userRepository.save(User.builder()
                    .leagueId(UUID.randomUUID())
                    .username(USERNAME_STARTS_WITH + UUID.randomUUID())
                    .avatarHashKey(UUID.randomUUID().toString())
                    .status(UserStatusType.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .roles(roleRepository.findAllByNameIn(userRoleTypes))
                    .build());

        }

        return userForRoles;
    }

    private User getUserForRoles(List<UserRoleType> userRoleTypes) {
        for (User user : userRepository.findAll()) {
            List<UserRoleType> userRolesList = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());

            if (userRolesList.containsAll(userRoleTypes)) {
                return user;
            }
        }

        return null;
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
