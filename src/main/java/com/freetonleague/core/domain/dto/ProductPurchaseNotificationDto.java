package com.freetonleague.core.domain.dto;

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
    private String identifier = UUID.randomUUID().toString();

    private String leagueId;

    private String username;

    private String productId;

    private String productName;

    private Double purchaseQuantity;

    private PurchaseStateType purchaseState;

    private List<ProductPropertyDto> selectedProductParameters;

    @ApiModelProperty(notes = "Comment from user about purchase")
    private String buyerComment;

    @ApiModelProperty(notes = "Comment from manager about purchase. Available only in Edit mode.")
    private String managerComment;

    private LocalDateTime createdAt;
}
