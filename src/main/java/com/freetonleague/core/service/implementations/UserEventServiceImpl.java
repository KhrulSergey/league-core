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
import com.freetonleague.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserEventServiceImpl implements UserEventService {

    private final EventService eventService;
    private final UserService userService;
    private final FinancialClientService financialClientService;

    private final Set<UUID> cachedUserLeagueId = Collections.synchronizedSet(new HashSet<>());

    @Override
    public EventDto add(EventDto event) {
        log.info("! handle add EventDto");
        return null;
    }

    //every 2 hours, timeout before start 1 min
    @Scheduled(fixedRate = 2 * 60 * 60 * 1000, initialDelay = 60 * 1000)
    void monitor() {
        log.debug("^ Run UserEventService monitor");

        final Map<UUID, User> idToUserMap = this.getIdToUserMap();
        final Set<UUID> keys = new HashSet<>(idToUserMap.keySet());

        if (idToUserMap.isEmpty()) {
            log.debug("^ Initiated users were not found. UserEventService monitor waits.");
            return;
        }

        if (!cachedUserLeagueId.isEmpty() && cachedUserLeagueId.containsAll(keys)) {
            log.debug("^ User events cache was cleaned");
            cachedUserLeagueId.clear();
        } else {
            keys.removeAll(cachedUserLeagueId);
        }

        for (UUID selectedKey : keys) {
            User user = idToUserMap.get(selectedKey);
            this.tryMakeStatusUpdateOperations(user);
        }
    }

    private void tryMakeStatusUpdateOperations(User user) {
        log.debug("^ try to define events for user: {}", user.getLeagueId());
        final UserStatusType userStatus = user.getStatus();

        if (userStatus.isInitiated()) {
            this.handleUserStatusChange(user, UserStatusType.CREATED);
        }
        log.debug("^ user {} with status {} were checked, and added to cache", user.getLeagueId(), user.getStatus());
        cachedUserLeagueId.add(user.getLeagueId());
    }

    private Map<UUID, User> getIdToUserMap() {
        return Collections.unmodifiableMap(
                userService.getInitiatedUserList()
                        .stream()
                        .collect(Collectors.toMap(User::getLeagueId, user -> user)));
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
                .modelId(user.getLeagueId().toString())
                .modelData(updateFields)
                .createdDate(LocalDateTime.now())
                .build();
        try {
            log.debug("Not implement to send kafka event in handleUserStatusChange: {}", event);
//            eventService.sendEvent(event);
        } catch (Exception exc) {
            log.error("Error in handleStatusChange: {}", exc.getMessage());
        }
        user.setStatus(newUserStatusType);
        userService.edit(user);
    }

}
