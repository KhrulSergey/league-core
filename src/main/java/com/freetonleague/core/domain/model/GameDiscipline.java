package com.freetonleague.core.domain.model;

import com.sun.istack.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

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

    @OneToMany(mappedBy = "gameDiscipline", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<GameDisciplineSettings> gameDisciplineSettings;
    @Column(name = "logo_file_name")
    private String logoFileName;
    @Column(name = "is_active")
    private boolean isActive;
}
