package com.freetonleague.core.controller;

import com.freetonleague.core.domain.model.Participant;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.ParticipantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping(path = ParticipantController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Participant Management Controller")
public class ParticipantController {

    private final ParticipantService participantService;

    public static final String BASE_PATH = "/api/participant";
    public static final String PATH_GET_ME = "/me";
    public static final String PATH_GET_CURRENT = "/current";
    public static final String PATH_REGISTER = "/register";


    @ApiOperation("Getting data about all participation for current user from session")
    @GetMapping(path = PATH_GET_CURRENT)
    public ResponseEntity<List<Participant>> getCurrent(@ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(participantService.getAllParticipation(user), HttpStatus.OK);
    }
}
