package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    //    @Query(value = "SELECT * FROM team_management.teams where id in (SELECT p.team_id
//    FROM team_management.participants AS p WHERE p.league_id=)",  nativeQuery = true)
    @Query(value = "select t from Team t where t.id in (select p from TeamParticipant p where p.user = :user)")
    List<Team> findAllByUserParticipation(@Param("user") User user);
}
