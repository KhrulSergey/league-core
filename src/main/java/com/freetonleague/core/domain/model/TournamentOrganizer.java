package com.freetonleague.core.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(schema = "public", name = "tournament_organizers")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "tournament_organizers_id_seq", allocationSize = 1, schema = "public")
public class TournamentOrganizer extends BaseEntity {

    //Properties
    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "league_id", referencedColumnName = "league_id", nullable = false)
    private User user;

    @Transient
    private List<String> privilegeList;
}
