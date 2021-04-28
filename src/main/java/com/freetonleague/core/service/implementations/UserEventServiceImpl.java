package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.EventDto;
import com.freetonleague.core.domain.enums.AccountHolderType;
import com.freetonleague.core.domain.enums.EventOperationType;
import com.freetonleague.core.domain.enums.EventProducerModelType;
import com.freetonleague.core.domain.enums.UserStatusType;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.EventService;
import com.freetonleague.core.service.FinancialClientService;
import com.freetonleague.core.service.UserEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserEventServiceImpl implements UserEventService {

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
    public void processUserStatusChange(User user, UserStatusType newUserStatusType) {
        log.debug("^ new status changed for user {} with new status {}.", user, newUserStatusType);
        if (newUserStatusType.isCreated()) {
            financialClientService.createAccountByHolderInfo(user.getLeagueId(),
                    AccountHolderType.USER, user.getUsername());
        }
    }

    private void handleUserStatusChange(User user, UserStatusType newUserStatusType) {
        Map<String, Object> updateFields = Map.of(
                "status", newUserStatusType
        );

        EventDto event = EventDto.builder()
                .id(UUID.randomUUID().toString())
                .message("Change status of User")
                .eventOperationType(EventOperationType.UPDATE_FIELDS)
                .eventTopic(EventProducerModelType.USER)
                .modelId(user.getId().toString())
                .modelData(updateFields)
                .createdDate(LocalDateTime.now())
                .build();
        try {
            eventService.sendEvent(event);
        } catch (Exception exc) {
            log.error("Error in handleStatusChange: {}", exc.getMessage());
        }
    }

}
