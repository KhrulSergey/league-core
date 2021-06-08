package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.NotificationType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPublicDto {

    @ApiModelProperty(required = true)
    @NotBlank
    private String title;

    @ApiModelProperty(required = true)
    @NotBlank
    @JsonProperty("text")
    private String message;

    @ApiModelProperty(required = true)
    @NonNull
    private NotificationType type;
}
