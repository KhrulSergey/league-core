package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.PurchaseStateType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * View of user proposals to docket (Universal lists)
 */
@SuperBuilder
@NoArgsConstructor
@Data
public class ProductPurchaseDto {

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
    private Long productId;

    private Map<String, Object> selectedProductParameters;

    @Builder.Default
    @DecimalMin("0.0000000000000001")
    private Double purchaseQuantity = 1.0;

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double purchaseTotalAmount;

    @Builder.Default
    @NotNull
    @ApiModelProperty(required = true)
    private PurchaseStateType state = PurchaseStateType.CREATED;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty(readOnly = true, notes = "Saved version of purchase payment transaction")
    private List<AccountTransactionInfoDto> purchasePaymentList;
}
