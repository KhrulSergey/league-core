package com.freetonleague.core.domain.dto.docket;

import com.freetonleague.core.domain.enums.AccessType;
import com.freetonleague.core.domain.enums.DocketStatusType;
import com.freetonleague.core.domain.enums.DocketSystemType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * View of docket (universal lists) for different purpose
 */
@SuperBuilder
@NoArgsConstructor
@Data
public class DocketDto {

    private Long id;

    //Properties
    @NotBlank
    @ApiModelProperty(required = true)
    private String name;

    private String description;

    @Size(max = 900)
    @ApiModelProperty(allowableValues = "range[0, 900]")
    private String textLabel;

    @Builder.Default
    @NotNull
    @ApiModelProperty(required = true)
    private DocketStatusType status = DocketStatusType.CREATED;

    @Builder.Default
    @NotNull
    @ApiModelProperty(required = true)
    private DocketSystemType systemType = DocketSystemType.DEFAULT;

    @Builder.Default
    @NotNull
    @ApiModelProperty(required = true)
    private AccessType accessType = AccessType.FREE_ACCESS;

    @Size(max = 450)
    @ApiModelProperty(allowableValues = "range[0, 450]")
    private String imageUrl;

    @ApiModelProperty(notes = "Default or minimum participation fee")
    private Double participationFee;

    @ApiModelProperty(notes = "Maximum participation fee")
    private Double maxParticipationFee;

    @ApiModelProperty(notes = "optional")
    private Integer maxProposalCount;

    private Long promoId;

}
