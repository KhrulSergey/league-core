package com.freetonleague.core.config;

import com.freetonleague.core.domain.model.Session;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component("auditorProvider")
public class SecurityAuditorAwareImpl implements AuditorAware<User> {

    private final SessionService sessionService;

    @Value("${freetonleague.service.league-finance.service-token:Pu6ThMMkF4GFTL5Vn6F45PHSaC193232HGdsQ}")
    private String leagueFinanceServiceToken;

    @Override
    public Optional<User> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isNull(authentication)
                || authentication.getPrincipal().equals("anonymousUser")
                || !authentication.isAuthenticated()) {
            Session session = sessionService.loadServiceByToken(leagueFinanceServiceToken);
            return nonNull(session) ?
                    Optional.of(session.getUser())
                    : Optional.empty();
        }
        return Optional.of((User) authentication.getPrincipal());
    }
}

