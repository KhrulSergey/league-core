package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.enums.TournamentMatchSeriesType;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@Table(schema = "public", name = "tournament_matches")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "tournament_matches_id_seq", schema = "public", allocationSize = 1)
public class TournamentMatch extends ExtendedBaseEntity {

    @NotNull
    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "series_id")
    private TournamentSeries tournamentSeries;

    @OneToMany(mappedBy = "tournamentMatch", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<TournamentMatchRival> rivals;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "winner_match_rival_id")
    private TournamentMatchRival matchWinner;

    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TournamentStatusType status;

    @NotNull
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TournamentMatchSeriesType typeForSeries;

    @Column(name = "start_planned_at")
    private LocalDateTime startPlannedDate;

    @Column(name = "start_at")
    private LocalDateTime startDate;

    @Column(name = "finished_at")
    private LocalDateTime finishedDate;
}

