package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    @Override
    <S extends Team> S saveAndFlush(S entity);

    @Override
    Optional<Team> findById(Long teamID);
}
