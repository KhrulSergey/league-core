package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.NotificationDto;
import com.freetonleague.core.service.NotificationService;
import com.freetonleague.core.service.kafka.MessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Objects.isNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MessageProducer producer;
    private final Validator validator;

    @Override
    public void sendNotification(NotificationDto notificationDto) {
        if (!this.verifyNotification(notificationDto)) {
            log.error("!> requesting sendNotification for not properly defined notificationDto {}. Check evoking clients",
                    notificationDto);
            return;
        }
        executor.submit(() -> sendNotificationToKafka(notificationDto));
    }

    private void sendNotificationToKafka(NotificationDto notificationDto) {
        ListenableFuture<SendResult<String, NotificationDto>> listenableFuture = this.producer.sendNotificationMessage(notificationDto);
        try {
            SendResult<String, NotificationDto> result = listenableFuture.get();
            log.info("Produced: notification id '{}' message: '{}' \ntopic: '{}', offset: '{}', partition: '{}', value size: '{}'",
                    notificationDto.getIdentifier(),
                    notificationDto.getMessage(),
                    notificationDto.getTitle(), result.getRecordMetadata().offset(),
                    result.getRecordMetadata().partition(), result.getRecordMetadata().serializedValueSize());
        } catch (ExecutionException | InterruptedException exc) {
            log.error("!> error while send notification {}. Check stack trace {}",
                    notificationDto, exc);
        }
    }


    private boolean verifyNotification(NotificationDto notificationDto) {
        if (isNull(notificationDto)) {
            log.error("!> requesting verifyNotification for NULL notificationDto. Check evoking clients");
            return false;
        }
        Set<ConstraintViolation<NotificationDto>> violations = validator.validate(notificationDto);
        if (!violations.isEmpty()) {
            log.error("!> requesting verifyNotification for notificationDto '{}' with constraint violations: '{}'. " +
                    "Check evoking clients", notificationDto, violations);
            return false;
        }
        return true;
    }
}
