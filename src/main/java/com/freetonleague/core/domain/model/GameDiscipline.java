package com.freetonleague.core.domain.model;

import com.sun.istack.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@Table(schema = "public", name = "game_disciplines")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "game_disciplines_id_seq", schema = "public", allocationSize = 1)
public class GameDiscipline extends ExtendedBaseEntity {

    //Properties
    @NotNull
    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "description")
    private String description;
}
