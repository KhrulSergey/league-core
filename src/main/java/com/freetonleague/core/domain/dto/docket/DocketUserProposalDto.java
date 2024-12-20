package com.freetonleague.core.domain.dto.docket;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.dto.finance.AccountTransactionInfoDto;
import com.freetonleague.core.domain.dto.UserDto;
import com.freetonleague.core.domain.enums.ParticipationStateType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * View of user proposals to docket (Universal lists)
 */
@SuperBuilder
@NoArgsConstructor
@Data
public class DocketUserProposalDto {

    private Long id;

    //Properties
    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UserDto user;

    @ApiModelProperty(required = true)
    @NotBlank
    private String leagueId;

    @NotNull
    @ApiModelProperty(required = true)
    private Long docketId;

    private String textLabelAnswer;

    private Double participationFee;

    @Builder.Default
    @NotNull
    @ApiModelProperty(required = true)
    private ParticipationStateType state = ParticipationStateType.CREATED;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty(readOnly = true)
    private List<AccountTransactionInfoDto> participatePaymentList;

    private String promoCode;

}
