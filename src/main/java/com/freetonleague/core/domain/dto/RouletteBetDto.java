package com.freetonleague.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouletteBetDto {

    private UUID userId;

    private Integer ticketNumber;

    private Double tonAmount;

    private Long chance;

}
