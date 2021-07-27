package com.freetonleague.core.repository.tournament;

import com.freetonleague.core.domain.model.tournament.GameDiscipline;
import com.freetonleague.core.domain.model.tournament.GameDisciplineSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameDisciplineSettingsRepository extends JpaRepository<GameDisciplineSettings, Long>,
        JpaSpecificationExecutor<GameDisciplineSettings> {

    @Query(value = "select s from GameDisciplineSettings s where s.gameDiscipline = :gameDiscipline and s.isPrimary = true")
    GameDisciplineSettings findByTruePrimaryAndGameDiscipline(@Param("gameDiscipline") GameDiscipline gameDiscipline);

    boolean existsByName(String name);
}
