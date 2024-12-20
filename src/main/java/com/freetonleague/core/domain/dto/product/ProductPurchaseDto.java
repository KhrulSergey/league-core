package com.freetonleague.core.domain.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.dto.UserDto;
import com.freetonleague.core.domain.dto.finance.AccountTransactionInfoDto;
import com.freetonleague.core.domain.enums.product.ProductPurchaseStateType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

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

    private List<ProductPropertyDto> selectedProductParameters;

    @Builder.Default
    @DecimalMin("0.0000000000000001")
    private Double purchaseQuantity = 1.0;

    @Builder.Default
    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double purchaseTotalAmount = 0.0;

    @Builder.Default
    @NotNull
    @ApiModelProperty(required = true)
    private ProductPurchaseStateType state = ProductPurchaseStateType.CREATED;

    @ApiModelProperty(notes = "Comment from user about purchase")
    private String buyerComment;

    @ApiModelProperty(notes = "Comment from manager about purchase. Available only in Edit mode.")
    private String managerComment;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty(readOnly = true, notes = "Saved version of purchase payment transaction")
    private List<AccountTransactionInfoDto> purchasePaymentList;
}
