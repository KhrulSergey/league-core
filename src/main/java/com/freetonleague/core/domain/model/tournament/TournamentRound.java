package com.freetonleague.core.domain.model.tournament;

import com.freetonleague.core.domain.enums.tournament.GameIndicatorType;
import com.freetonleague.core.domain.enums.tournament.TournamentRoundType;
import com.freetonleague.core.domain.enums.tournament.TournamentStatusType;
import com.freetonleague.core.domain.model.ExtendedBaseEntity;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true, of = {"name", "roundNumber", "status", "isLast"})
@Getter
@Setter
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Table(schema = "public", name = "tournament_rounds")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "tournament_rounds_id_seq", schema = "public", allocationSize = 1)
public class TournamentRound extends ExtendedBaseEntity {

    @NotNull
    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "tournamentRound", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TournamentSeries> seriesList;

    /**
     * Position of round for current tournament
     */
    @NotNull
    @Min(1)
    @Column(name = "round_number")
    private Integer roundNumber;

    /**
     * Sign if round is last for tournament
     */
    @Builder.Default
    @Column(name = "is_last")
    private Boolean isLast = false;

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

    @Getter
    @Type(type = "jsonb")
    @Column(name = "game_indicator_multipliers", columnDefinition = "jsonb")
    private Map<GameIndicatorType, Double> gameIndicatorMultipliersMap;

    public void setStatus(TournamentStatusType status) {
        prevStatus = this.status;
        this.status = status;
    }

    public boolean isStatusChanged() {
        return !this.status.equals(this.prevStatus);
    }

    public boolean getLast() {
        return isTrue(isLast);
    }

}
