package com.freetonleague.core.domain.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocketPromoCreationFilter {

    @Min(1)
    @Max(100)
    @NotNull
    private Integer maxUsages;

}
