package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.enums.TeamStateType;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(schema = "team_management", name = "teams")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "teams_id_seq", schema = "team_management", allocationSize = 1)
public class Team extends BaseEntity {

    //Properties
    @NotNull
    @Column(name = "name", unique = true)
    private String name;

    @OneToOne
    @JoinColumn(name = "captain_id", unique = true)
    private Participant captain;

    @OneToMany(mappedBy = "team", cascade = CascadeType.REFRESH, orphanRemoval = false, fetch = FetchType.EAGER)
    private List<Participant> participantList;

    //TODO сделать конвертер для сохранения и получения пути к файлу Лого (аналогично тому, который будет в league-id)
    @Column(name = "team_logo_file_name")
    private String teamLogoFileName;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TeamStateType status;
}
