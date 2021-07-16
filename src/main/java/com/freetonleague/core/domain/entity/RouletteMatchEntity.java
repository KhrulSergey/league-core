package com.freetonleague.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.freetonleague.core.domain.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(schema = "public", name = "roulette_matches")
@SequenceGenerator(name = "base_entity_seq", sequenceName = "dockets_id_seq", schema = "public", allocationSize = 1)
public class RouletteMatchEntity extends BaseEntity {

    private boolean finished;

    private String randomOrgId;

    private LocalDateTime shouldStartedAfter;

    private Long lastTicketNumber;

    private Long betSum;

    private LocalDateTime finishedAt;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RouletteBetEntity> bets;

    @ManyToOne
    @JoinColumn(name = "winner_bet_id")
    private RouletteBetEntity winnerBet;

}
