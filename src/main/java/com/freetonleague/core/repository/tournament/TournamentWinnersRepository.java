package com.freetonleague.core.repository.tournament;

import com.freetonleague.core.domain.model.tournament.TournamentWinner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TournamentWinnersRepository extends JpaRepository<TournamentWinner, Long>,
        JpaSpecificationExecutor<TournamentWinner> {

}
