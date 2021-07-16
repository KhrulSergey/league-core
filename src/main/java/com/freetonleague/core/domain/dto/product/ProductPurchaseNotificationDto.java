package com.freetonleague.core.domain.dto.product;

import com.freetonleague.core.domain.enums.PurchaseStateType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPurchaseNotificationDto {

    @Builder.Default
    @Setter(AccessLevel.NONE)
    @ApiModelProperty(required = true)
    private String identifier = UUID.randomUUID().toString();

    @ApiModelProperty(required = true)
    private String leagueId;

    @ApiModelProperty(required = true)
    private String username;

    @ApiModelProperty(required = true)
    private String productId;

    @ApiModelProperty(required = true)
    private String productName;

    @ApiModelProperty(required = true)
    private Double purchaseQuantity;

    @ApiModelProperty(required = true)
    private PurchaseStateType purchaseState;

    private List<ProductPropertyDto> selectedProductParameters;

    @ApiModelProperty(notes = "Comment from user about purchase")
    private String buyerComment;

    @ApiModelProperty(notes = "Comment from manager about purchase. Available only in Edit mode.")
    private String managerComment;

    @ApiModelProperty(required = true)
    private LocalDateTime createdAt;
}
