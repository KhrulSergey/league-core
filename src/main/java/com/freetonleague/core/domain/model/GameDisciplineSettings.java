package com.freetonleague.core.domain.model;

import com.freetonleague.core.domain.enums.GameIndicatorType;
import com.freetonleague.core.util.GameIndicatorConverter;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLHStoreType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@TypeDef(name = "hstore", typeClass = PostgreSQLHStoreType.class)
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
     * Hash map of indicators with optimal values
     */
    @Type(type = "hstore")
    @Column(name = "game_optimal_indicators", columnDefinition = "hstore")
    private Map<GameIndicatorType, Object> gameOptimalIndicators;


    public Map<GameIndicatorType, Object> getGameOptimalIndicators() {
        return GameIndicatorConverter.convertAndValidate(gameOptimalIndicators);
    }
}
