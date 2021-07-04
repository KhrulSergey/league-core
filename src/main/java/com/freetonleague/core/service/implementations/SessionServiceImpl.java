package com.freetonleague.core.service.implementations;

import com.freetonleague.core.cloudclient.LeagueIdClientService;
import com.freetonleague.core.domain.dto.SessionDto;
import com.freetonleague.core.domain.model.Session;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.UnauthorizedException;
import com.freetonleague.core.exception.config.ExceptionMessages;
import com.freetonleague.core.mapper.SessionMapper;
import com.freetonleague.core.repository.SessionRepository;
import com.freetonleague.core.service.SessionService;
import com.freetonleague.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

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

    @Value("${freetonleague.service.league-finance.service-token}")
    private String leagueFinanceServiceToken;

    @Override
    public Session get(String token) {
        return sessionRepository.findByToken(token);
    }


    public Session saveSession(Session session) {
        log.debug("^ trying to save new session: '{}'", session);
        return sessionRepository.save(session);
    }

    /**
     * Returns session if it was found in DB or imported from LeagueId-module
     */
    @Override
    public Session loadByToken(String token) {
        if (isBlank(token)) {
            log.debug("^ session token was Blank in loadByToken()");
            return null;
        }
        log.debug("^ trying to find session by token: '{}'", token);
        Session session = sessionRepository.findByToken(token);
        if (isNull(session)) {
            // trying to find session in LeagueId-module
            log.debug("^ session with token: '{}' wasn't found in DB. Trying to load session from LeagueId-module", token);
            SessionDto sessionDto = leagueIdSessionClient.getSession(token);
            if (nonNull(sessionDto)) {
                log.debug("^ found session with token: '{}' in LeagueId-module", token);
                //trying to find user in DB or import from LeagueId-module
                User user = userService.loadWithLeagueId(sessionDto.getUserLeagueId(), token);
                if (nonNull(user)) {
                    // create new session
                    log.debug("^ trying to save new session with token: '{}' for user: '{}'", token, user);
                    session = this.saveFromLeagueId(sessionDto, user);
                }
            }
        }
        return session;
    }

    /**
     * Returns service session if token is correct
     */
    @Override
    public Session loadServiceByToken(String accessToken) {
        if (isBlank(accessToken) || !accessToken.equals(this.leagueFinanceServiceToken)) {
            log.error("!!> accessToken: '{}' for service session was Blank or not correct. Service authorize request rejected.", accessToken);
            return null;
        }
        log.debug("^ trying to find service session by accessToken: '{}'", accessToken);
        User user = userService.findByUsername("SERVICE_" + accessToken);
        Session session = this.getByUser(user);
        if (isNull(session)) {
            log.debug("^ active session for service user wasn't found in DB. trying to create new session for Service user '{}'", user);
            session = this.createSessionForServiceUser(user);
        }
        return session;
    }

    @Override
    public void revoke(Session session) {
        if (nonNull(session) && session.getExpiration().isAfter(LocalDateTime.now())) {
            session.setExpiration(LocalDateTime.now());
            this.saveSession(session);
        }
    }

    @Override
    public void revoke(String token) {
        this.revoke(this.get(token));
    }

    @Override
    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * Create new session from LeagueID-module info
     */
    private Session saveFromLeagueId(SessionDto sessionDto, User user) {
        if (isNull(sessionDto) || isNull(user)) {
            log.error("!> error while creating new session for user '{}' and session data '{}' ", user, sessionDto);
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_SESSION_ERROR,
                    "Some error while creating session in Core-module for " + user + " with data " + sessionDto);
        }
        Session session = mapper.fromDto(sessionDto);
        session.setUser(user);
        return this.saveSession(session);
    }

    /**
     * Create new session for service user onlye in Core-module
     */
    public Session createSessionForServiceUser(User user) {
        if (isNull(user)) {
            log.error("!> error while creating new session for NULL user. Check evoking params ");
            throw new UnauthorizedException(ExceptionMessages.AUTHENTICATION_SESSION_ERROR,
                    "Some error while creating session in Core-module for service user");
        }
        Session session = Session.builder()
                .user(user)
                .expiration(LocalDateTime.now().plusSeconds(sessionDurationInSec))
                .authProvider("CORE")
                .token(UUID.randomUUID().toString())
                .build();
        return this.saveSession(session);
    }

    private Session getByUser(User user) {
        return sessionRepository.findByUserAndExpirationAfter(user, LocalDateTime.now());
    }
}
