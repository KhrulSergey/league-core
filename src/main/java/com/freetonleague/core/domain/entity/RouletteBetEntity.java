package com.freetonleague.core.domain.entity;

import com.freetonleague.core.domain.model.BaseEntity;
import com.freetonleague.core.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(schema = "public", name = "roulette_match_bets")
public class RouletteBetEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private RouletteMatchEntity match;

    private Long ticketNumberFrom;
    private Long ticketNumberTo;

    private Long tonAmount;

}
