package com.freetonleague.core.repository;


import com.freetonleague.core.domain.model.Session;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс доступа к данным сущности "Сессия" из БД
 */
public interface SessionRepository extends JpaRepository<Session, Long>,
        JpaSpecificationExecutor<Session> {

    Session findByToken(String token);

    Session deleteByToken(String token);

    Session findByUserAndExpirationAfter(User user, LocalDateTime localDateTime);

    List<Session> findAllByUserAndExpirationAfter(User user, LocalDateTime localDateTime);

}
