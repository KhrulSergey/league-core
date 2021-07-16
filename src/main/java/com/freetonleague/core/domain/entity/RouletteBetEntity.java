package com.freetonleague.core.domain.entity;

import com.freetonleague.core.domain.model.BaseEntity;
import com.freetonleague.core.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(schema = "public", name = "roulette_match_bet")
public class RouletteBetEntity extends BaseEntity {

    @ManyToOne
    private User user;

    @ManyToOne
    private RouletteMatchEntity match;

    private Long ticketNumberFrom;
    private Long ticketNumberTo;

    private Long tonAmount;

}
