package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.dto.MatchPropertyDto;
import com.freetonleague.core.domain.enums.TournamentStatusType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
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

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id")
    private TournamentSeries tournamentSeries;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "tournamentMatch", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<TournamentMatchRival> matchRivalList;

    /**
     * Winner of current (finished) match. If null - then there were a dead heat
     */
    @EqualsAndHashCode.Exclude
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "winner_match_rival_id")
    private TournamentMatchRival matchWinner;

    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TournamentStatusType status;

    @Transient
    private TournamentStatusType prevStatus;

    @Builder.Default
    @Column(name = "match_number_in_series")
    private Integer matchNumberInSeries = 1;

    // TODO Delete column until 01/09/2021 if no use
    /**
     * Not usable field
     */
    @Transient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Column(name = "type")
    private String type;

    @Column(name = "start_planned_at")
    private LocalDateTime startPlannedDate;

    @Column(name = "start_at")
    private LocalDateTime startDate;

    @Column(name = "finished_at")
    private LocalDateTime finishedDate;

    /**
     * List of indicators with optimal values (serialized)
     */
    @Type(type = "jsonb")
    @Column(name = "match_properties", columnDefinition = "jsonb")
    private List<MatchPropertyDto> matchPropertyList;

    public void setStatus(TournamentStatusType status) {
        prevStatus = this.status;
        this.status = status;
    }

    public boolean isStatusChanged() {
        return !this.status.equals(this.prevStatus);
    }
}

