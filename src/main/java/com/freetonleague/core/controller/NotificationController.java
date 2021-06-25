package com.freetonleague.core.controller;

import com.freetonleague.core.domain.dto.NotificationPublicDto;
import com.freetonleague.core.domain.dto.ProductPurchaseNotificationDto;
import com.freetonleague.core.service.RestNotificationFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = NotificationController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Notification Send Controller")
public class NotificationController {

    public static final String BASE_PATH = "/api/notification";
    public static final String PATH_CREATE_MASS = "/mass/";
    public static final String PATH_RECEIVE_PURCHASE_NOTIFY = "/test/purchase";

    private final RestNotificationFacade restNotificationFacade;

    /**
     * The same value as from "${freetonleague.session.service-token-name}"
     */
    private final String staticServiceTokenName = "service_token";


    @ApiOperation("Create new docket on platform")
    @PostMapping(path = PATH_CREATE_MASS)
    public ResponseEntity<Boolean> createMassNotification(@RequestBody NotificationPublicDto notificationDto,
                                                          @RequestParam UUID[] leagueIdArray) {
        List<UUID> leagueIdList = List.of(leagueIdArray);
        return new ResponseEntity<>(restNotificationFacade.createMassNotification(notificationDto, leagueIdList), HttpStatus.CREATED);
    }

    @ApiOperation("Test receive purchase notification (only mock use)")
    @PostMapping(path = PATH_RECEIVE_PURCHASE_NOTIFY)
    public ResponseEntity<Boolean> testReceivePurchaseNotification(@RequestParam(staticServiceTokenName) String serviceToken,
                                                                   @RequestBody ProductPurchaseNotificationDto purchaseNotificationDto) {
        log.debug("^ received purchase notification '{}' with serviceToken '{}'. Do nothing.", purchaseNotificationDto, serviceToken);
        return new ResponseEntity<>(true, HttpStatus.CREATED);
    }
}
