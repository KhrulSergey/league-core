package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.TournamentSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TournamentSettingsRepository extends JpaRepository<TournamentSettings, Long>,
        JpaSpecificationExecutor<TournamentSettings> {

}
