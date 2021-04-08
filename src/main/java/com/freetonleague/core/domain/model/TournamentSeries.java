package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.enums.TournamentSeriesType;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.lang.Math.toIntExact;
import static java.util.Objects.nonNull;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
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
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @OneToMany(mappedBy = "tournamentSeries", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    private List<TournamentMatch> matches;

    /**
     * Position of all series for current tournament (round number)
     */
    @NotNull
    @Min(1)
    @Column(name = "series_sequence_position")
    private Integer seriesSequencePosition;

    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TournamentStatusType status;

    @Transient
    private TournamentStatusType prevStatus;

    @NotNull
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TournamentSeriesType type;

    /**
     * How many match should be in current series
     */
    @NotNull
    @Column(name = "goal_match_count")
    private Integer goalMatchCount;

    /**
     * How many rivals should be in current series
     */
    @NotNull
    @Column(name = "goal_match_rival_count")
    private Integer goalMatchRivalCount;

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

    public int teamProposalCount() {
        int count = 0;
        if (nonNull(matches)) {
            count = toIntExact(matches.stream()
                    .map(TournamentMatch::getRivals).filter(Objects::nonNull)
                    .mapToInt(Set::size).count()
            );
        }
        return count;
    }

    public int teamProposalParticipantCount() {
        int count = 0;
        if (nonNull(matches)) {
            count = toIntExact(matches.stream()
                    .map(TournamentMatch::getRivals).filter(Objects::nonNull)
                    .flatMap(Set::stream).filter(Objects::nonNull)
                    .map(TournamentMatchRival::getRivalParticipants)
                    .mapToInt(Set::size).count()
            );
        }
        return count;
    }
}
