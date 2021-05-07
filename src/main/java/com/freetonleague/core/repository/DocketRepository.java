package com.freetonleague.core.repository;

import com.freetonleague.core.domain.enums.DocketStatusType;
import com.freetonleague.core.domain.model.Docket;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocketRepository extends JpaRepository<Docket, Long>,
        JpaSpecificationExecutor<Docket> {

    /**
     * Returns all dockets with status in the list and pageable params
     */
    Page<Docket> findAllByStatusIn(Pageable pageable, List<DocketStatusType> statusList);

    /**
     * Returns all dockets with status in the list, created by specified user and pageable params
     */
    Page<Docket> findAllByStatusInAndCreatedBy(Pageable pageable, List<DocketStatusType> statusList, User user);

    /**
     * Returns all dockets with created by specified user
     */
    Page<Docket> findAllByCreatedBy(Pageable pageable, User user);

    /**
     * Returns all dockets with status in the list and pageable params
     */
    @Query(value = "select d from Docket d where d.status in :activeStatusList")
    List<Docket> findAllActive(@Param("activeStatusList") List<DocketStatusType> activeStatusList);
}
