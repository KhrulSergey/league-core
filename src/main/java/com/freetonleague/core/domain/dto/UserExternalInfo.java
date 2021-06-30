package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

@SuperBuilder
@NoArgsConstructor
@Data
public class UserExternalInfo {
    private String externalProvider;

    @SerializedName("id")
    @JsonProperty("id")
    @NotBlank
    @Column(name = "external_id")
    private String externalId;

    @SerializedName("username")
    @JsonProperty("username")
    private String externalUsername;

    @SerializedName("realname")
    @JsonProperty("realname")
    private String name;

    private String externalEmail;
}
