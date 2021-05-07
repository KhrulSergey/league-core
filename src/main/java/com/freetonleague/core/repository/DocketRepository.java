package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.Docket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DocketRepository extends JpaRepository<Docket, Long>,
        JpaSpecificationExecutor<Docket> {

//    /**
//     * Returns all tournaments with status in the list and pageable params
//     */
//    Page<Tournament> findAllByStatusIn(Pageable pageable, List<TournamentStatusType> statusList);
//
//    /**
//     * Returns all tournaments with status in the list, created by specified user and pageable params
//     */
//    Page<Tournament> findAllByStatusInAndCreatedBy(Pageable pageable, List<TournamentStatusType> statusList, User user);
//
//    /**
//     * Returns all tournaments with created by specified user
//     */
//    Page<Tournament> findAllByCreatedBy(Pageable pageable, User user);
//
//    /**
//     * Returns all tournaments with status in the list and pageable params
//     */
//    @Query(value = "select t from Tournament t where t.status in :activeStatusList")
//    List<Tournament> findAllActive(@Param("activeStatusList") List<TournamentStatusType> activeStatusList);
}
