package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.enums.TeamParticipantStatusType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@EqualsAndHashCode(callSuper = true, exclude = "team")
@Getter
@Setter
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(schema = "team_management", name = "team_participants")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "team_participants_id_seq", allocationSize = 1, schema = "team_management")
public class TeamParticipant extends BaseEntity implements Serializable {

    //Properties
    @ManyToOne
    @JoinColumn(name = "league_id", referencedColumnName = "league_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_id", unique = true)
    private Team team;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TeamParticipantStatusType status;

    @CreationTimestamp
    @Column(name = "join_at")
    private LocalDateTime joinAt;

    @Setter(AccessLevel.NONE)
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Override
    public String toString() {
        String userString = nonNull(user) ? "user=" + user : "";
        String teamString = nonNull(team) ? ", team=" + team.getId() : "";
        return "Participant{" +
                userString +
                teamString +
                ", status=" + status +
                ", joinAt=" + joinAt +
                ", deletedAt=" + deletedAt +
                '}';
    }
}
