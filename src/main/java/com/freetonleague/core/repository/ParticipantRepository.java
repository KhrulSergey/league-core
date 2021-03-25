package com.freetonleague.core.repository;

import com.freetonleague.core.domain.model.Participant;
import com.freetonleague.core.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    /**
     * Add participant to team by his id and team id.
     *
     * @param participant participant what will be added
     * @return added participant
     */
    @Override
    <S extends Participant> S saveAndFlush(S participant);

    /**
     * Delete participant from team by his id and team id.
     *
     * @param participantId participant's ID what will be deleted
     * @return deleted participant
     */
    @Modifying
    @Query(value = "update Participant s set s.status = 'DELETED', s.deletedAt = CURRENT_TIMESTAMP where s.id = :participantId")
    Optional<Participant> delete(@Param("participantId") Long participantId);

    List<Participant> findAllByUser (User user);
}
