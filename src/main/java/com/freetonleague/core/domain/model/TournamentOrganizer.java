package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.enums.TournamentOrganizerStatusType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@Entity
@SuperBuilder
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Table(schema = "public", name = "tournament_organizers")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "tournament_organizers_id_seq", allocationSize = 1, schema = "public")
public class TournamentOrganizer extends ExtendedBaseEntity {

    //Properties
    @NotNull
    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "league_id", referencedColumnName = "league_id", nullable = false)
    private User user;

    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TournamentOrganizerStatusType status;
    /**
     * Schema of distribution prize fund between winners
     */
    @Type(type = "jsonb")
    @Column(name = "privilege_list", columnDefinition = "jsonb")
    private Set<String> privilegeList;
}
