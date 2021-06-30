package com.freetonleague.core.domain.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public class UserImportExternalInfo {

    @CsvBindByPosition(position = 0)
    @CsvBindByName(column = "service", required = true)
    private String externalProvider;

    @CsvBindByPosition(position = 1)
    @CsvBindByName(column = "user_id", required = true)
    private String externalId;

    @CsvBindByPosition(position = 2)
    @CsvBindByName(column = "user_address", required = true)
    private String accountExternalAddress;

    @Builder.Default
    @CsvBindByPosition(position = 3)
    @CsvBindByName(column = "leagueId", required = true)
    private String leagueId = "none";
}
