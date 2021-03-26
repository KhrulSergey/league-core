package com.freetonleague.core.security;

import com.freetonleague.core.domain.model.Session;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.SessionService;
import com.freetonleague.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

import static java.util.Objects.nonNull;

/**
 * Class for pre filtering request and search for token in header or JSESSIONID
 */

@RequiredArgsConstructor
public class AuthenticationCustomFilter extends UsernamePasswordAuthenticationFilter {

    private final SessionService sessionService;
    private final UserService userService;

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


        if (nonNull(token)) {
            Session session = sessionService.loadByToken(token);
            if (nonNull(session) && !session.isExpired()) {
                User user = session.getUser();
                if (nonNull(user)) {
                    this.setUserToContext(user, session);
                }
            }
        }
        chain.doFilter(req, res);
    }

    private void setUserToContext(User user, Session session) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
    }
}
