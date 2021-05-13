package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.AccessType;
import com.freetonleague.core.domain.enums.DocketStatusType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * View of universal docket (lists) for different purpose
 */
@SuperBuilder
@NoArgsConstructor
@Data
public class DocketDto {

    private Long id;

    //Properties
    @NotBlank
    private String name;

    private String description;

    @Size(max = 900)
    private String textLabel;

    @Builder.Default
    @NotNull
    private DocketStatusType status = DocketStatusType.CREATED;

    @Builder.Default
    @NotNull
    private AccessType accessType = AccessType.FREE_ACCESS;

    @Size(max = 450)
    private String imageUrl;

    @Builder.Default
    private Double participationFee = 0.0;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<DocketUserProposalDto> userProposalList;
}
