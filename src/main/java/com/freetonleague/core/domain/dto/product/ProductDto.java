package com.freetonleague.core.domain.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.AccessType;
import com.freetonleague.core.domain.enums.product.ProductStatusType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * View of universal docket (lists) for different purpose
 */
@SuperBuilder
@NoArgsConstructor
@Data
public class ProductDto {

    private Long id;

    @NotBlank
    @ApiModelProperty(required = true)
    private String name;

    private String description;

    private String imageUrl;

    /**
     * Detailed parameters (text labels) for product
     */
    @ApiModelProperty(notes = "Detailed parameters (text labels) for product")
    private List<ProductPropertyDto> productParameters;

    @ApiModelProperty(required = true)
    @Builder.Default
    @NotNull
    private ProductStatusType status = ProductStatusType.ACTIVE;

    @ApiModelProperty(required = true)
    @Builder.Default
    @NotNull
    private AccessType accessType = AccessType.PAID_ACCESS;

    @Builder.Default
    @DecimalMin("0.0000000000000001")
    private Double cost = 0.0;

    private Double quantityInStock;

    @DecimalMin("0.0000000000000001")
    private Double possibleQuantityStep;

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime archivedAt;

}
