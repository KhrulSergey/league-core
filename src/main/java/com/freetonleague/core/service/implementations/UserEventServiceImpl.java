package com.freetonleague.core.service.implementations;

import com.freetonleague.core.cloudclient.LeagueIdClientService;
import com.freetonleague.core.domain.dto.EventDto;
import com.freetonleague.core.domain.dto.UserDto;
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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserEventServiceImpl implements UserEventService {

    private final EventService eventService;
    private final UserService userService;
    private final LeagueIdClientService leagueIdClientService;
    private final FinancialClientService financialClientService;

    private final Set<UUID> cachedInitiatedUserLeagueId = Collections.synchronizedSet(new HashSet<>());

    private final Set<UUID> cachedActiveUserLeagueId = Collections.synchronizedSet(new HashSet<>());

    @Override
    public EventDto add(EventDto event) {
        log.info("! handle add EventDto");
        return null;
    }

    //every 10 minutes, timeout before start 1 min
    @Scheduled(fixedRate = 10 * 60 * 1000, initialDelay = 60 * 1000)
    private void monitorForActiveUsers() {
        log.debug("^ Run UserEventService monitor For Update Active Users");

        final Map<UUID, User> activeUserIdToUserMap = this.getActiveUserIdToUserMap();
        final Set<UUID> keys = new HashSet<>(activeUserIdToUserMap.keySet());

        if (activeUserIdToUserMap.isEmpty()) {
            log.debug("^ Initiated users were not found. UserEventService monitorForActiveUsers waits.");
            return;
        }

        if (!cachedActiveUserLeagueId.isEmpty() && cachedActiveUserLeagueId.containsAll(keys)) {
            log.debug("^ User update events cache was cleaned");
            cachedActiveUserLeagueId.clear();
        } else {
            keys.removeAll(cachedActiveUserLeagueId);
        }

        int countSelected = (int) Math.ceil(keys.size() * 0.15); //15%

        List<UUID> selectedKeys = keys.stream().limit(countSelected).collect(Collectors.toList());

        for (UUID selectedKey : selectedKeys) {
            User user = activeUserIdToUserMap.get(selectedKey);
            this.tryUpdateInfoFromLeagueIdModule(user);
        }
    }

    //every 2 hours, timeout before start 2 min
    @Scheduled(fixedRate = 2 * 60 * 60 * 1000, initialDelay = 2 * 60 * 1000)
    private void monitorForInitiatedUsers() {
        log.debug("^ Run UserEventService monitor For Initiated Users");

        final Map<UUID, User> initiatedUserIdToUserMap = this.getInitiatedUserIdToUserMap();
        final Set<UUID> keys = new HashSet<>(initiatedUserIdToUserMap.keySet());

        if (initiatedUserIdToUserMap.isEmpty()) {
            log.debug("^ Initiated users were not found. UserEventService monitorForInitiatedUsers waits.");
            return;
        }

        if (!cachedInitiatedUserLeagueId.isEmpty() && cachedInitiatedUserLeagueId.containsAll(keys)) {
            log.debug("^ User initiated events cache was cleaned");
            cachedInitiatedUserLeagueId.clear();
        } else {
            keys.removeAll(cachedInitiatedUserLeagueId);
        }

        for (UUID selectedKey : keys) {
            User user = initiatedUserIdToUserMap.get(selectedKey);
            this.tryMakeStatusUpdateOperations(user);
        }
    }

    private void tryUpdateInfoFromLeagueIdModule(User user) {
        log.debug("^ try to define update events for user: {}", user.getLeagueId());
        cachedInitiatedUserLeagueId.add(user.getLeagueId());
        try {
            UserDto updatedUser = leagueIdClientService.getUserByLeagueId(user.getLeagueId());
            if (nonNull(updatedUser) && nonNull(updatedUser.getUpdatedAt())
                    && updatedUser.getUpdatedAt().isAfter(user.getUpdatedAt())) {

                log.debug("^ user.id {} were choose to updated info {} in monitorForActiveUsers", user.getLeagueId(), updatedUser);

                user.setAvatarHashKey(updatedUser.getAvatarHashKey());
                user.setEmail(updatedUser.getEmail());
                user.setDiscordId(updatedUser.getDiscordId());
                user.setName(updatedUser.getName());

                User savedUser = userService.edit(user);
                if (isNull(savedUser)) {
                    log.debug("^ user.id {} were not saved in DB with data {}. Check stackTrace", user.getLeagueId(), updatedUser);
                }
            }

        } finally {
            log.debug("^ user.id {} were checked in monitorForActiveUsers and added to cache", user.getLeagueId());
            cachedActiveUserLeagueId.add(user.getLeagueId());
        }
    }

    private void tryMakeStatusUpdateOperations(User user) {
        log.debug("^ try to define events for user: {}", user.getLeagueId());
        final UserStatusType userStatus = user.getStatus();

        if (userStatus.isInitiated()) {
            this.handleUserStatusChange(user, UserStatusType.CREATED);
        }
        log.debug("^ user {} with status {} were checked, and added to cache", user.getLeagueId(), user.getStatus());
        cachedInitiatedUserLeagueId.add(user.getLeagueId());
    }


    private Map<UUID, User> getActiveUserIdToUserMap() {
        return Collections.unmodifiableMap(
                userService.findActiveUserList()
                        .stream()
                        .collect(Collectors.toMap(User::getLeagueId, user -> user)));
    }

    private Map<UUID, User> getInitiatedUserIdToUserMap() {
        return Collections.unmodifiableMap(
                userService.findInitiatedUserList()
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
