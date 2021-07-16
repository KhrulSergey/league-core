package com.freetonleague.core.repository.tournament;

import com.freetonleague.core.domain.model.tournament.TournamentSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TournamentSettingsRepository extends JpaRepository<TournamentSettings, Long>,
        JpaSpecificationExecutor<TournamentSettings> {

}
