package com.freetonleague.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freetonleague.core.domain.enums.NotificationType;
import com.freetonleague.core.domain.model.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Objects.nonNull;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    @Builder.Default
    @Setter(AccessLevel.NONE)
    private String identifier = UUID.randomUUID().toString();

    @JsonIgnore
    private User user;

    private UUID leagueId;

    @NotBlank
    private String title;

    @NotBlank
    @JsonProperty("text")
    private String message;

    private NotificationType type;

    @Builder.Default
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt = LocalDateTime.now();

    public String getLeagueId() {
        return nonNull(user) ? user.getLeagueId().toString() : leagueId.toString();
    }
}
