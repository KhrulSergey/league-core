package com.freetonleague.core.service.implementations;


import com.freetonleague.core.domain.model.Session;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.repository.SessionRepository;
import com.freetonleague.core.service.SessionService;
import com.freetonleague.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@Transactional(propagation = Propagation.REQUIRES_NEW)
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final UserService userService;
    private final SessionRepository sessionRepository;

    @Value("${session.duration:604800}")
    private Long sessionDurationInSec;


    @Override
    public Session get(String token) {
        return sessionRepository.findByToken(token);
    }

    @Override
    public Session getByUser(User user) {
        return sessionRepository.findByUserAndExpirationAfter(user, LocalDateTime.now());
    }

    @Override
    public void revoke(Session session) {
        if (nonNull(session) && session.getExpiration().isAfter(LocalDateTime.now())) {
            session.setExpiration(LocalDateTime.now());
            sessionRepository.save(session);
        }
    }

    @Override
    public void revoke(String token) {
        this.revoke(this.get(token));
    }

    @Override
    public User getCurrentUser(HttpServletRequest request) {
        return get(request.getHeader("X-Auth-Token")).getUser();
    }

    private List<Session> getValidByUser(User user) {
        return sessionRepository.findAllByUserAndExpirationAfter(user, LocalDateTime.now());
    }
}
