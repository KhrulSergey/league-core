package com.freetonleague.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouletteBetEntity {

    private User user;

    private Integer ticketNumber;

    private Double tonAmount;

    private Long chance;

}
