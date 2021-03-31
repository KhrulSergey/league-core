package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.TournamentOrganizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TournamentOrganizerRepository extends JpaRepository<TournamentOrganizer, Long>,
        JpaSpecificationExecutor<TournamentOrganizer> {

}
