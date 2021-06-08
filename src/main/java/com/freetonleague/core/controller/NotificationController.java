package com.freetonleague.core.controller;

import com.freetonleague.core.domain.dto.NotificationPublicDto;
import com.freetonleague.core.service.RestNotificationFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = NotificationController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Notification Send Controller")
public class NotificationController {

    public static final String BASE_PATH = "/api/notification";
    public static final String PATH_CREATE_MASS = "/mass/";

    private final RestNotificationFacade restNotificationFacade;


    @ApiOperation("Create new docket on platform")
    @PostMapping(path = PATH_CREATE_MASS)
    public ResponseEntity<Boolean> createMassNotification(@RequestBody NotificationPublicDto notificationDto,
                                                          @RequestParam UUID[] leagueIdArray) {
        List<UUID> leagueIdList = List.of(leagueIdArray);
        return new ResponseEntity<>(restNotificationFacade.createMassNotification(notificationDto, leagueIdList), HttpStatus.CREATED);
    }
}
