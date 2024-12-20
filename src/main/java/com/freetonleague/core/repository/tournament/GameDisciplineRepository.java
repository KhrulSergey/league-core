package com.freetonleague.core.repository.tournament;

import com.freetonleague.core.domain.model.tournament.GameDiscipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GameDisciplineRepository extends JpaRepository<GameDiscipline, Long>,
        JpaSpecificationExecutor<GameDiscipline> {

    boolean existsByName(String name);

}
