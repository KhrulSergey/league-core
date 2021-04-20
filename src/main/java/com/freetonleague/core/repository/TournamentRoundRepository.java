package com.freetonleague.core.repository;

import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.model.Tournament;
import com.freetonleague.core.domain.model.TournamentRound;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface TournamentRoundRepository extends JpaRepository<TournamentRound, Long>,
        JpaSpecificationExecutor<TournamentRound> {

    Page<TournamentRound> findAllByTournament(Pageable pageable, Tournament tournament);

    /**
     * Returns first tournament round with status in the list and specified tournament
     */
    TournamentRound findByStatusInAndTournament(List<TournamentStatusType> activeStatusList, Tournament tournament);
}
