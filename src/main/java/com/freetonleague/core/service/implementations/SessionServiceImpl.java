package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.SessionDto;
import com.freetonleague.core.domain.model.Session;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.ExceptionMessages;
import com.freetonleague.core.exception.UnauthorizedException;
import com.freetonleague.core.mapper.SessionMapper;
import com.freetonleague.core.repository.SessionRepository;
import com.freetonleague.core.cloudclient.LeagueIdClientService;
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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Transactional(propagation = Propagation.REQUIRES_NEW)
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final UserService userService;
    private final SessionRepository sessionRepository;
    private final LeagueIdClientService leagueIdSessionClient;
    private final SessionMapper mapper;

    @Value("${session.duration:604800}")
    private Long sessionDurationInSec;


    @Override
    public Session get(String token) {
        return sessionRepository.findByToken(token);
    }

    /** Returns session if it was found in DB or imported from LeagueId-module*/
    @Override
    public Session loadByToken(String token) {
        if (isBlank(token)) {
            return null;
        }
        Session session = sessionRepository.findByToken(token);
        if (isNull(session)) {
            // trying to find session in LeagueId-module
            SessionDto sessionDto = leagueIdSessionClient.getSession(token);
            if(nonNull(sessionDto)) {
                //trying to find user in DB or import from LeagueId-module
                User user = userService.loadWithLeagueId(sessionDto.getUserLeagueId(), token);
                if(nonNull(user)){
                    // create new session
                    session = this.saveFromLeagueId(sessionDto, user);
                }
            }
        }
        return session;
    }

    @Override
    public Session getByUser(User user) {
        return sessionRepository.findByUserAndExpirationAfter(user, LocalDateTime.now());
    }

    /** Create new session from LeagueID-module info */
    private Session saveFromLeagueId(SessionDto sessionDto, User user) {
        if (isNull(sessionDto) || isNull(user)) {
            log.error("!> error while creating new session for user {} and session data {} ", user, sessionDto);
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_SESSION_ERROR,
                    "Some error while creating session in Core-module for " + user + " with data " + sessionDto);
        }
        Session session = mapper.fromDto(sessionDto);
        session.setUser(user);
        return sessionRepository.save(session);
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
