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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(schema = "public", name = "roulette_matches")
public class RouletteMatchEntity extends BaseEntity {

    private boolean finished;

    private String randomOrgId;

    private LocalDateTime shouldStartedAfter;

    private Long lastTicketNumber;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RouletteBetEntity> bets;

    @ManyToOne
    private RouletteBetEntity winnerBet;

}
