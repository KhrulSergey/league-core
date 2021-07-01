package com.freetonleague.core.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(schema = "public", name = "docket_promos_usages")
public class DocketPromoUsageEntity extends BaseEntity {

    @ManyToOne
    private DocketPromoEntity promo;

    @ManyToOne
    private User user;

}
