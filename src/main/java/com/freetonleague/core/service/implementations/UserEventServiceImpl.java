package com.freetonleague.core.service.implementations;

import com.freetonleague.core.cloudclient.LeagueIdClientService;
import com.freetonleague.core.config.properties.AppUserProperties;
import com.freetonleague.core.domain.dto.*;
import com.freetonleague.core.domain.dto.finance.AccountInfoDto;
import com.freetonleague.core.domain.enums.*;
import com.freetonleague.core.domain.model.finance.AccountTransaction;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.exception.CustomUnexpectedException;
import com.freetonleague.core.service.EventService;
import com.freetonleague.core.service.FinancialClientService;
import com.freetonleague.core.service.UserEventService;
import com.freetonleague.core.service.UserService;
import com.freetonleague.core.service.financeUnit.FinancialUnitService;
import com.freetonleague.core.util.CsvFileUtil;
import com.freetonleague.core.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
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
    private final FinancialUnitService financialUnitService;
    private final AppUserProperties appUserProperties;

    private final Set<UUID> cachedInitiatedUserLeagueId = Collections.synchronizedSet(new HashSet<>());

    private final Set<UUID> cachedActiveUserLeagueId = Collections.synchronizedSet(new HashSet<>());


    @Value("${app.user.import:false}")
    private boolean importUser;


    @Value("${app.user.import-test-data:true}")
    private boolean importTestData;

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
            if (isNull(user)) {
                log.debug("^ User with leagueId '{}' was not found in Core DB. Continue to monitor users", selectedKey);
                continue;
            }
            this.tryUpdateInfoFromLeagueIdModule(user);
        }
    }

    //every 2 hours, timeout before start 2 min
//    @Scheduled(fixedRate = 2 * 60 * 60 * 1000, initialDelay = 2 * 60 * 1000)
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

    //every 3 days, timeout before start 1 min
//    @Scheduled(fixedRate = 3 * 24 * 60 * 60 * 1000, initialDelay = 1 * 60 * 1000)
    public void importUsersDataFromFile() {
        log.debug("^ Run importUsersDataFromFile task to Import Users");
        if (importUser) {
            try {
                String importFileName = importTestData ? "user_data_raw_temp.csv" : "user_data_raw.csv";
                InputStream importFileStream = new ClassPathResource(importFileName).getInputStream();
                String exportFilePath = System.getProperty("user.dir") + File.separator + "user_data_export.csv";

                this.importUserFromInfoListToDisk(importFileStream, exportFilePath);
            } catch (IOException e) {
                throw new CustomUnexpectedException("Error while import user data from disk" + e.getMessage());
            }
        }
        log.debug("^ End importUsersDataFromFile task to Import Users. All data saved to disk");
    }

    private void importUserFromInfoListToDisk(InputStream importFileStream, String exportFilePath) {
        List<UserImportExternalInfo> userImportInfoList = CsvFileUtil.readCsvUserImportInfo(true, importFileStream);

        userImportInfoList = userImportInfoList.stream().map(this::importUserFromInfo)
                .filter(Objects::nonNull).collect(Collectors.toList());
        CsvFileUtil.writeCsvUserImportInfo(userImportInfoList, exportFilePath);
    }

    /**
     * Return updated user info with bank account address after import him to platform
     */
    private UserImportExternalInfo importUserFromInfo(UserImportExternalInfo userImportInfo) {
        String username = StringUtil.generateRandomName();
        UserExternalInfo userExternalInfo = UserExternalInfo.builder()
                .externalProvider(userImportInfo.getExternalProvider().toUpperCase())
                .externalId(userImportInfo.getExternalId())
                .externalUsername(username)
                .name("Unknown")
                .build();
        User user = userService.importUserToPlatform(userExternalInfo);
        if (nonNull(user)) {
            userImportInfo.setAccountExternalAddress(user.getBankAccountAddress());
            userImportInfo.setLeagueId(user.getLeagueId().toString());
        } else {
            userImportInfo = null;
        }
        return userImportInfo;
    }

    private void tryUpdateInfoFromLeagueIdModule(User user) {
        log.debug("^ try to define update events for user: '{}'", user.getLeagueId());
        try {
            UserDto updatedUser = leagueIdClientService.getUserByLeagueId(user.getLeagueId());
            if (nonNull(updatedUser) && nonNull(updatedUser.getUpdatedAt())
                    && updatedUser.getUpdatedAt().isAfter(user.getUpdatedAt())) {

                log.debug("^ user.id '{}' were choose to updated info '{}' in monitorForActiveUsers", user.getLeagueId(), updatedUser);

                user.setAvatarHashKey(updatedUser.getAvatarHashKey());
                user.setEmail(updatedUser.getEmail());
                user.setDiscordId(updatedUser.getDiscordId());
                user.setName(updatedUser.getName());

                User savedUser = userService.edit(user);
                if (isNull(savedUser)) {
                    log.debug("^ user.id '{}' were not saved in DB with data '{}'. Check stackTrace", user.getLeagueId(), updatedUser);
                }
            }

        } finally {
            log.debug("^ user.id '{}' were checked in monitorForActiveUsers and added to cache", user.getLeagueId());
            cachedActiveUserLeagueId.add(user.getLeagueId());
        }
    }

    private void tryMakeStatusUpdateOperations(User user) {
        log.debug("^ try to define events for user: '{}'", user.getLeagueId());
        final UserStatusType userStatus = user.getStatus();

        if (userStatus.isInitiated()) {
            this.handleUserStatusChange(user, UserStatusType.CREATED);
        }
        log.debug("^ user '{}' with status '{}' were checked, and added to cache", user.getLeagueId(), user.getStatus());
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
    public AccountInfoDto processUserStatusChange(User user, UserStatusType newUserStatusType) {
        log.debug("^ new status changed for user '{}' with new status '{}'.", user, newUserStatusType);
        AccountInfoDto accountInfoDto = null;
        if (newUserStatusType.isCreated()) {
            accountInfoDto = financialClientService.createAccountByHolderInfo(user.getLeagueId(),
                    AccountHolderType.USER, user.getUsername());

            Map<String, Double> bonusMap = appUserProperties.getUtmSourceRegisterBonusMap();

            if (bonusMap != null && bonusMap.containsKey(user.getUtmSource())) {
                Double bonusAmount = bonusMap.get(user.getUtmSource());

                AccountTransaction accountTransaction = AccountTransaction.builder()
                        .amount(bonusAmount)
                        .targetAccount(financialUnitService.getAccountByGUID(UUID.fromString(accountInfoDto.getGUID())))
                        .transactionType(TransactionType.DEPOSIT)
                        .transactionTemplateType(TransactionTemplateType.EXTERNAL_PROVIDER)
                        .status(AccountTransactionStatusType.FINISHED)
                        .build();

                financialUnitService.createTransaction(accountTransaction);
            }
        }
        return accountInfoDto;
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
                .createdDate(LocalDate.now())
                .build();
        try {
            log.debug("Not implement to send kafka event in handleUserStatusChange: '{}'", event);
//            eventService.sendEvent(event);
        } catch (Exception exc) {
            log.error("Error in handleStatusChange: '{}'", exc.getMessage());
        }
        user.setStatus(newUserStatusType);
        userService.edit(user);
    }
}
