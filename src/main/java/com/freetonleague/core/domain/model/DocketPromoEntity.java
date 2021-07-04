package com.freetonleague.core.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(schema = "public", name = "docket_promos")
public class DocketPromoEntity extends BaseEntity {

    private String promoCode;

    private Integer maxUsages;

    private boolean enabled;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "promo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DocketPromoUsageEntity> usages;

}
