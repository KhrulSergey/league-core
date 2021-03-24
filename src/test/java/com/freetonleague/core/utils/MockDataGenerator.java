package com.freetonleague.core.utils;

import com.freetonleague.core.domain.enums.UserStatusType;
import com.freetonleague.core.domain.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class MockDataGenerator {

    public static User generateUser() {
        return User.builder()
                .id((long) (Math.random() * 100))
                .leagueId(UUID.randomUUID())
                .username(UUID.randomUUID().toString())
                .avatarFileName(UUID.randomUUID().toString())
                .status(UserStatusType.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

}
