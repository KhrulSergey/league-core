package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.freetonleague.core.domain.enums.ResourcePrivacyType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

@SuperBuilder
@Data
@NoArgsConstructor
public class MediaResourceDto {

    /**
     * Not null for adding resource
     */
    @ApiModelProperty(required = true)
    @NotNull
    private String rawData;

    @ApiModelProperty(readOnly = true)
    private String hashKey;

    @ApiModelProperty(required = true)
    @Size(max = 200)
    private String name;

    @ApiModelProperty(required = true)
    @NotNull
    private ResourcePrivacyType privacyType;

    /**
     * Owner reference (League-id for user, CoreId for team)
     * Used only for files that can be accessed by one owner.
     */
    private UUID privacyOwnerGUID;

    @ApiModelProperty(required = true)
    @NotNull
    private UUID creatorGUID;

    // readonly properties
    @ApiModelProperty(readOnly = true)
    private LocalDateTime createdAt;

    @ApiModelProperty(readOnly = true)
    private LocalDateTime updatedAt;

    @ApiModelProperty(readOnly = true)
    private String resourceType;

    @ApiModelProperty(readOnly = true)
    private Integer sizeInBytes;

    @ApiModelProperty(readOnly = true)
    private JsonNode resourceMetaData;

    @ApiModelProperty(readOnly = true)
    private String status;
}
