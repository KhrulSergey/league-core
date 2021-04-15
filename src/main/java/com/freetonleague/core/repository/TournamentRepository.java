package com.freetonleague.core.repository;

import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.model.Tournament;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TournamentRepository extends JpaRepository<Tournament, Long>,
        JpaSpecificationExecutor<Tournament> {

    /**
     * Returns all tournaments with status in the list and pageable params
     */
    Page<Tournament> findAllByStatusIn(Pageable pageable, List<TournamentStatusType> statusList);

    /**
     * Returns all tournaments with status in the list, created by specified user and pageable params
     */
    Page<Tournament> findAllByStatusInAndCreatedBy(Pageable pageable, List<TournamentStatusType> statusList, User user);

    /**
     * Returns all tournaments with created by specified user
     */
    Page<Tournament> findAllByCreatedBy(Pageable pageable, User user);

    /**
     * Returns all tournaments with status in the list and pageable params
     */
    @Query(value = "select t from Tournament t where t.status in :activeStatusList")
    List<Tournament> findAllActive(@Param("activeStatusList") List<TournamentStatusType> activeStatusList);
}
