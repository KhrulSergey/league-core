package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.NewsStatusType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Universal docket (lists) for different purpose
 */
@Data
public class NewsDto {

    private Long id;

    @NotBlank
    @ApiModelProperty(required = true)
    private String title;

    private String theme;

    private String imageUrl;

    @NotBlank
    @ApiModelProperty(required = true)
    private String description;

    private NewsStatusType status;

    private List<String> tags;

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @ApiModelProperty(readOnly = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
}
