package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.TeamParticipant;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamParticipantRepository extends JpaRepository<TeamParticipant, Long> {
    /**
     * Add participant to team by his id and team id.
     *
     * @param teamParticipant participant what will be added
     * @return added participant
     */
    @Override
    <S extends TeamParticipant> S saveAndFlush(S teamParticipant);

    /**
     * Expel (exclude) participant from his team.
     *
     * @param teamParticipant participant's that will be exclude
     * @return excluded participant
     */
    @Modifying
    @Query(value = "update TeamParticipant s set s.status = 'DELETED', s.deletedAt = CURRENT_TIMESTAMP where s = :participant")
    Optional<TeamParticipant> expel(@Param("participant") TeamParticipant teamParticipant);

    List<TeamParticipant> findAllByUser(User user);
}
