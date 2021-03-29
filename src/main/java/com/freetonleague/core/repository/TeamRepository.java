package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long>,
        JpaSpecificationExecutor<Team> {

    /**
     * Find all teams with participation of specified user
     */
    @Query(value = "select t from Team t where t in (select p.team from TeamParticipant p where p.user = :user)")
    List<Team> findAllByUserParticipation(@Param("user") User user);

    Team findByName(String name);
}
