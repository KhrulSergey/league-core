package com.freetonleague.core.repository;

import com.freetonleague.core.domain.entity.RouletteMatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;

public interface RouletteMatchRepository extends JpaRepository<RouletteMatchEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    RouletteMatchEntity findLockedByFinishedFalse();

}
