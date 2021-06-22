package com.freetonleague.core.cloudclient;

import com.freetonleague.core.domain.dto.ProductPurchaseNotificationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Rest client for sending data to Telegram External Client
 */
@FeignClient(name = "telegram-client", url = "${freetonleague.service.telegram-client.url}")
public interface TelegramClientCloud {

    /**
     * The same value as from "${freetonleague.session.service-token-name}"
     */
    String staticServiceTokenName = "service_token";

    @PostMapping("/purchase")
    Boolean sendPurchaseNotification(@RequestParam(staticServiceTokenName) String serviceToken,
                                     @RequestBody ProductPurchaseNotificationDto purchaseNotificationDto);
}
