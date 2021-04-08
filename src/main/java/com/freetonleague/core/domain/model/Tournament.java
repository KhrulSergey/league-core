package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.enums.TournamentAccessType;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.enums.TournamentSystemType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
public class Tournament extends ExtendedBaseEntity {

    //Properties
    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "game_discipline_id")
    private GameDiscipline gameDiscipline;

    /**
     * Chosen game discipline settings
     */
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "game_disciplines_settings_id")
    private GameDisciplineSettings gameDisciplineSettings;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TournamentOrganizer> tournamentOrganizerList;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TournamentTeamProposal> tournamentTeam;

    /**
     * Current tournament status
     */
    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TournamentStatusType status;

    @Transient
    private TournamentStatusType prevStatus;

    /**
     * Type of financial settings to participate in tournament
     */
    @NotNull
    @Column(name = "access_type")
    @Enumerated(EnumType.STRING)
    private TournamentAccessType accessType;

    /**
     * Type of grid generation algorithm for creating matches
     */
    @NotNull
    @Column(name = "system_type")
    @Enumerated(EnumType.STRING)
    private TournamentSystemType systemType;

    /**
     * Prototype for ref to Bank-Account entity for current tournament
     */
    @Transient
    private Long fundAccountId;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    private List<TournamentSeries> tournamentSeriesList;

    /**
     * List of tournament winners with places
     */
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TournamentWinner> winnerTeamProposal;

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
    @OneToOne(mappedBy = "tournament", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private TournamentSettings tournamentSettings;

    public void setStatus(TournamentStatusType status) {
        prevStatus = this.status;
        this.status = status;
    }

    public boolean isStatusChanged() {
        return !this.status.equals(this.prevStatus);
    }
}
