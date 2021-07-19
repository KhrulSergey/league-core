package com.freetonleague.core.domain.model.tournament;

import com.freetonleague.core.domain.dto.tournament.GameDisciplineIndicatorDto;
import com.freetonleague.core.domain.model.ExtendedBaseEntity;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Entity
@Table(schema = "public", name = "game_disciplines_settings")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "game_disciplines_settings_id_seq", schema = "public", allocationSize = 1)
public class GameDisciplineSettings extends ExtendedBaseEntity {

    //Properties
    @NotNull
    @Column(name = "name", unique = true)
    private String name;

    @NotNull
    @Column(name = "is_primary")
    private Boolean isPrimary;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "game_discipline_id")
    private GameDiscipline gameDiscipline;

    /**
     * Count of rivals to fight in match
     */
    @NotNull
    @Builder.Default
    @Column(name = "match_rival_count")
    private Integer matchRivalCount = 2;

    /**
     * Count of rivals to be kicked off (drop out) from series. Default Value.
     */
    @NotNull
    @Builder.Default
    @Column(name = "series_rival_kick_off_default_count")
    private Integer seriesRivalKickOffDefaultCount = 1;

    /**
     * List of indicators with optimal values (serialized)
     */
    @NotNull
    @NotEmpty
    @Type(type = "jsonb")
    @Column(name = "game_optimal_indicators", columnDefinition = "jsonb")
    private List<GameDisciplineIndicatorDto> gameOptimalIndicators;
}

