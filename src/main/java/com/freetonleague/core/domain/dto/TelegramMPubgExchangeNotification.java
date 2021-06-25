package com.freetonleague.core.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TelegramMPubgExchangeNotification {

    private Double tonAmount;
    private Double ucAmount;
    private String pubgId;

}
