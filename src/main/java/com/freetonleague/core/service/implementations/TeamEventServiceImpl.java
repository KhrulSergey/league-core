package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.EventDto;
import com.freetonleague.core.domain.enums.AccountHolderType;
import com.freetonleague.core.domain.enums.TeamStateType;
import com.freetonleague.core.domain.model.Team;
import com.freetonleague.core.service.EventService;
import com.freetonleague.core.service.FinancialClientService;
import com.freetonleague.core.service.TeamEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TeamEventServiceImpl implements TeamEventService {

    private final EventService eventService;
    private final FinancialClientService financialClientService;

    @Override
    public EventDto add(EventDto event) {
        log.info("! handle add EventDto");
        return null;
    }

    /**
     * Process user status changing
     */
    @Override
    public void processTeamStatusChange(Team team, TeamStateType newTeamStateType) {
        log.debug("^ new status changed for team {} with new status {}.", team, newTeamStateType);
        if (newTeamStateType.isCreated()) {
            financialClientService.createAccountByHolderInfo(team.getCoreId(),
                    AccountHolderType.TEAM, team.getName());
        }
    }
}
