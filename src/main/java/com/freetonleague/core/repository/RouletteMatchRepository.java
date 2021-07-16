package com.freetonleague.core.repository;

import com.freetonleague.core.domain.entity.RouletteMatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RouletteMatchRepository extends JpaRepository<RouletteMatchEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    RouletteMatchEntity findLockedByFinishedFalse();

    RouletteMatchEntity findByFinishedFalse();

    List<RouletteMatchEntity> findAllByFinishedTrueOrderByCreatedAt();

}
