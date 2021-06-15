package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamRepository extends JpaRepository<Team, Long>,
        JpaSpecificationExecutor<Team> {


    @Query(value = "select t from Team t where t.isVirtual = false ")
    Page<Team> findAllExceptVirtual(Pageable pageable);

    /**
     * Find all teams with participation of specified user
     */
    @Query(value = "select t from Team t where t.isVirtual = false and t in (select p.team from TeamParticipant p where p.user = :user) " +
            "and t.status <> com.freetonleague.core.domain.enums.TeamStateType.DELETED")
    Page<Team> findAllByUserParticipation(Pageable pageable, @Param("user") User user);

    Team findByName(String name);
}
