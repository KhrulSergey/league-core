package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.enums.TournamentType;
import com.sun.istack.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@Table(schema = "public", name = "tournaments")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "tournaments_id_seq", schema = "public", allocationSize = 1)
public class Tournament extends BaseEntity {

    //Properties
    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "game_discipline_id")
    private GameDiscipline gameDiscipline;

    /**
     * Specific game discipline settings
     */
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "game_disciplines_settings_id")
    private GameDisciplineSettings gameDisciplineSettings;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TournamentOrganizer> tournamentOrganizerList;

    /**
     * Current tournament status
     */
    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TournamentStatusType status;

    @NotNull
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TournamentType type;

    //Base settings
    @Column(name = "discord_channel_name")
    private String discordChannelName;

    @Column(name = "sign_up_start_at")
    private LocalDateTime signUpStartDate;

    @Column(name = "sign_up_ends_at")
    private LocalDateTime signUpEndDate;

    @Column(name = "start_planned_at")
    private LocalDateTime startPlannedDate;

    //Detailed settings
//    @OneToOne(mappedBy = "tournament",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "tournament_settings_id")
    private TournamentSettings tournamentSettings;

}
