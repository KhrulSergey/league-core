package com.freetonleague.core.domain.dto.docket;

import com.freetonleague.core.domain.dto.finance.AccountInfoDto;
import com.freetonleague.core.domain.dto.UserBonusDto;
import com.freetonleague.core.domain.enums.ParticipationStateType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * View of user proposals to Universal docket (lists)
 */
@SuperBuilder
@NoArgsConstructor
@Data
public class DocketUserProposalBonusDto {

    private Long id;

    private UserBonusDto user;

    private AccountInfoDto userAccount;

    private ParticipationStateType state;

    private Long docketId;
}
