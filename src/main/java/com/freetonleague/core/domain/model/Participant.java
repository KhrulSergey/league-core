package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.enums.ParticipantStatusType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(schema = "team_management", name = "participants")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "participants_id_seq", allocationSize = 1, schema = "team_management")
public class Participant extends BaseEntity implements Serializable {

    //Properties
    @ManyToOne
    @JoinColumn(name = "league_id", referencedColumnName = "league_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_id", unique = true)
    private Team team;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ParticipantStatusType status;

    @CreationTimestamp
    @Column(name = "join_at")
    private LocalDateTime joinAt;

    @Setter(AccessLevel.NONE)
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
