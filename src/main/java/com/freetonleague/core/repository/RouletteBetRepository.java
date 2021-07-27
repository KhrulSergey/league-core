package com.freetonleague.core.repository;

import com.freetonleague.core.domain.entity.RouletteBetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface RouletteBetRepository extends JpaRepository<RouletteBetEntity, Long> {

    @Query("SELECT sum(e.tonAmount) from RouletteBetEntity e")
    Long sumForAllTime();

    @Query("SELECT sum(e.tonAmount) from RouletteBetEntity e where e.createdAt < :end and e.createdAt > :start")
    Long sumForPeriod(LocalDateTime start, LocalDateTime end);

}
