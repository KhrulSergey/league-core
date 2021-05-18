package com.freetonleague.core.security;

import com.freetonleague.core.domain.model.Session;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.SessionService;
import lombok.RequiredArgsConstructor;
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

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Class for pre filtering request and search for token in header or JSESSIONID
 */

@RequiredArgsConstructor
public class AuthenticationCustomFilter extends UsernamePasswordAuthenticationFilter {

    private final SessionService sessionService;

    private final String headerTokenName;

    private final String serviceTokenName;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;

        String headerToken = request.getHeader(headerTokenName);
        String paramAccessToken = request.getParameter(serviceTokenName);

        Session session = null;
        if (!isBlank(headerToken)) {
            session = sessionService.loadByToken(headerToken);
        } else if (!isBlank(paramAccessToken)) {
            session = sessionService.loadServiceByToken(paramAccessToken);
        }
        if (nonNull(session) && !session.isExpired()) {
            User user = session.getUser();
            if (nonNull(user)) {
                this.setUserToContext(user, session);
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
