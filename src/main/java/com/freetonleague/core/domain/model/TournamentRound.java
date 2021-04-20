package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.enums.TournamentRoundType;
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

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@Table(schema = "public", name = "tournament_rounds")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "tournament_rounds_id_seq", schema = "public", allocationSize = 1)
public class TournamentRound extends ExtendedBaseEntity {

    @NotNull
    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @OneToMany(mappedBy = "tournamentRound", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TournamentSeries> seriesList;

    /**
     * Position of round for current tournament
     */
    @NotNull
    @Min(1)
    @Column(name = "round_number")
    private Integer roundNumber;

    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TournamentStatusType status;

    @Transient
    private TournamentStatusType prevStatus;

    @NotNull
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TournamentRoundType type;

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
}
