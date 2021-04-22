package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.enums.TournamentSeriesBracketType;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

/**
 * Entity to save collection of matches (one item in round tournament net)
 */
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
    @JoinColumn(name = "tournament_round_id")
    private TournamentRound tournamentRound;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "tournamentSeries", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TournamentMatch> matchList;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "tournamentSeries", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TournamentSeriesRival> rivalList;

    @EqualsAndHashCode.Exclude
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "winner_series_rival_id")
    private TournamentSeriesRival seriesWinner;

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

    @EqualsAndHashCode.Exclude
    @ManyToMany(targetEntity = TournamentSeries.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "tournament_series_parents",
            joinColumns = @JoinColumn(name = "parent_series_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "series_id", referencedColumnName = "id"))
    private Set<TournamentSeries> parentSeriesList;

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


    public Set<TournamentTeamProposal> getTeamProposalList() {
        return nonNull(rivalList) ? rivalList.parallelStream()
                .map(TournamentSeriesRival::getTeamProposal)
                .collect(Collectors.toSet())
                : null;
    }

    public TournamentTeamProposal getTeamProposalWinner() {
        return nonNull(seriesWinner) ? seriesWinner.getTeamProposal() : null;
    }

    //TODO нужно ли? Удалить до 01.05.2021
//    public int teamProposalCount() {
//        int count = 0;
//        if (nonNull(matchList)) {
//            count = toIntExact(matchList.stream()
//                    .map(TournamentMatch::getRivals).filter(Objects::nonNull)
//                    .mapToInt(Set::size).count()
//            );
//        }
//        return count;
//    }
//
//    public int teamProposalParticipantCount() {
//        int count = 0;
//        if (nonNull(matchList)) {
//            count = toIntExact(matchList.stream()
//                    .map(TournamentMatch::getRivals).filter(Objects::nonNull)
//                    .flatMap(Set::stream).filter(Objects::nonNull)
//                    .map(TournamentMatchRival::getRivalParticipants)
//                    .mapToInt(Set::size).count()
//            );
//        }
//        return count;
//    }
}
