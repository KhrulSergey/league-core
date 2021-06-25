package com.freetonleague.core.cloudclient;

import com.freetonleague.core.domain.dto.ProductPurchaseNotificationDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
@Component
public class TelegramClientService {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final TelegramClientCloud telegramClientCloud;

    @Value("${freetonleague.service.telegram-client.service-token}")
    private String telegramClientServiceToken;

    /**
     * Send new product purchase notification to telegram channel via external telegram service
     *
     * @param purchaseNotificationDto notification to send
     * @return true - if send was successful, false - if not send
     */
    public boolean sendNewPurchaseNotification(ProductPurchaseNotificationDto purchaseNotificationDto) {
        log.debug("^ try to send new purchase notification '{}'", purchaseNotificationDto);
        // TODO change behaivor to async until 01/10/21 or delete comment
//        executor.submit(() -> sendNotificationToKafka(purchaseNotificationDto));
        return this.sendPurchaseNotificationToTelegram(purchaseNotificationDto);
    }

    private Boolean sendPurchaseNotificationToTelegram(ProductPurchaseNotificationDto purchaseNotificationDto) {
        Boolean result = false;
        try {
            result = telegramClientCloud.sendPurchaseNotification(telegramClientServiceToken, purchaseNotificationDto);
        } catch (FeignException exc) {
            log.error("!> received new FeignException in TelegramClientService.sendNotificationToKafka message: '{}'. \nReturn success = false",
                    exc.getMessage(), exc);
        }
        return result;
    }
}
