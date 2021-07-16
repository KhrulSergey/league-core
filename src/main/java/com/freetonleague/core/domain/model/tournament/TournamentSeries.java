package com.freetonleague.core.domain.model.tournament;

import com.freetonleague.core.domain.enums.TournamentSeriesBracketType;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import com.freetonleague.core.domain.model.ExtendedBaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

/**
 * Entity to save collection of matches (one item in round tournament net)
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true, of = {"name", "status"})
@Getter
@Setter
@Entity
@Table(schema = "public", name = "tournament_series")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "tournament_series_id_seq", schema = "public", allocationSize = 1)
public class TournamentSeries extends ExtendedBaseEntity {

    @NotNull
    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "tournament_round_id")
    private TournamentRound tournamentRound;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "tournamentSeries", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TournamentMatch> matchList;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "tournamentSeries", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TournamentSeriesRival> seriesRivalList;

    @EqualsAndHashCode.Exclude
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "winner_series_rival_id")
    private TournamentSeriesRival seriesWinner;

    /**
     * Sign if series ends with a double AFK. If false and status=Finished then seriesWinner should be set.
     */
    @Builder.Default
    @JoinColumn(name = "has_no_winner")
    private Boolean hasNoWinner = false;

    /**
     * Bracket type for series
     */
    @NotNull
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TournamentSeriesBracketType type;

    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TournamentStatusType status;

    @Transient
    private TournamentStatusType prevStatus;

    @Builder.Default
    @Column(name = "is_incomplete")
    private Boolean isIncomplete = false;

    @EqualsAndHashCode.Exclude
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "tournament_series_parents",
            joinColumns = @JoinColumn(name = "child_series_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "parent_series_id", referencedColumnName = "id"))
    private List<TournamentSeries> parentSeriesList;

    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(name = "tournament_series_parents",
            joinColumns = @JoinColumn(name = "parent_series_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "child_series_id", referencedColumnName = "id"))
    private TournamentSeries childSeries;

    @Column(name = "start_planned_at")
    private LocalDateTime startPlannedDate;

    @Column(name = "start_at")
    private LocalDateTime startDate;

    @Column(name = "finished_at")
    private LocalDateTime finishedDate;

    public void setStatus(TournamentStatusType status) {
        prevStatus = this.status;
        this.status = status;
    }

    public boolean isStatusChanged() {
        return !this.status.equals(this.prevStatus);
    }

    public List<TournamentTeamProposal> getTeamProposalList() {
        return nonNull(seriesRivalList) ? seriesRivalList.parallelStream()
                .map(TournamentSeriesRival::getTeamProposal)
                .collect(Collectors.toList())
                : null;
    }

    public TournamentTeamProposal getTeamProposalWinner() {
        return nonNull(seriesWinner) ? seriesWinner.getTeamProposal() : null;
    }
}
