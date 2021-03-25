package com.freetonleague.core.security;

import com.freetonleague.core.domain.dto.UserDto;
import com.freetonleague.core.domain.model.Session;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.restclient.SessionCloudClient;
import com.freetonleague.core.service.SessionService;
import com.freetonleague.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Class for pre filtering request and search for token in header or JSESSIONID
 */

@RequiredArgsConstructor
public class AuthenticationCustomFilter extends UsernamePasswordAuthenticationFilter {

    private final SessionService sessionService;
    private final UserService userService;

    private final SessionCloudClient sessionCloudClient;

    @Value("${spring.session.token-name:token}")
    private String headerTokenName;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;

        String headerToken = request.getHeader("X-Auth-Token");
        String paramToken = request.getParameter("token");

        String token = Objects.isNull(headerToken)
                ? paramToken
                : headerToken;

        if(!isBlank(token)) {
            UserDto userInfo = sessionCloudClient.account(token);
        }

        if (nonNull(token)) {
            Session session = sessionService.get(token);
            if (nonNull(session) && !session.isExpired()) {
                User user = session.getUser();
                if (nonNull(user)) {
                    this.setUserToContext(user, session);
                }
            }
        }
        chain.doFilter(req, res);
    }

    private User getAuthorizedUserById(String leagueId, HttpServletRequest request) {
        try {
            return userService.get(UUID.fromString(leagueId));
        } catch (AuthenticationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            return null;
        }
    }

    private void setUserToContext(User user, Session session) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
    }
}
