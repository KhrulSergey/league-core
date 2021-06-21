package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.NotificationDto;
import com.freetonleague.core.domain.dto.NotificationPublicDto;
import com.freetonleague.core.exception.ValidationException;
import com.freetonleague.core.exception.config.ExceptionMessages;
import com.freetonleague.core.security.permissions.CanSendNotification;
import com.freetonleague.core.service.NotificationService;
import com.freetonleague.core.service.RestNotificationFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Slf4j
@RequiredArgsConstructor
@Service
public class RestNotificationFacadeImpl implements RestNotificationFacade {

    public final NotificationService notificationService;
    public final Validator validator;

    @CanSendNotification
    @Override
    public boolean createMassNotification(NotificationPublicDto notificationDto, List<UUID> leagueIdList) {
        // Verify Notification information
        Set<ConstraintViolation<NotificationPublicDto>> violations = validator.validate(notificationDto);
        if (!violations.isEmpty()) {
            log.debug("^ transmitted notificationDto: '{}' have constraint violations: '{}'", notificationDto, violations);
            throw new ConstraintViolationException(violations);
        }
        if (isEmpty(leagueIdList)) {
            log.warn("~ parameter 'leagueIdList' is empty for createMassNotification");
            throw new ValidationException(ExceptionMessages.NOTIFICATION_VALIDATION_ERROR, "leagueIdList",
                    "parameter leagueIdList is empty  for createMassNotification");
        }
        log.debug("^ try to send mass notifications {} to {} receivers", notificationDto, leagueIdList.size());
        leagueIdList.parallelStream()
                .map(leagueId -> NotificationDto.builder()
                        .title(notificationDto.getTitle())
                        .message(notificationDto.getMessage())
                        .type(notificationDto.getType())
                        .leagueId(leagueId)
                        .build())
                .forEach(notificationService::sendNotification);
        return true;
    }
}
