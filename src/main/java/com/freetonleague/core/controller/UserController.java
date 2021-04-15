package com.freetonleague.core.controller;

import com.freetonleague.core.domain.dto.UserDto;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.RestUserFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(path = UserController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "User-Core Management Controller")
public class UserController {

    public static final String BASE_PATH = "/api/user";
    public static final String PATH_GET = "/{league_id}";

    private final RestUserFacade restFacade;

    @ApiOperation("Get user info by league id")
    @GetMapping(path = PATH_GET)
    public ResponseEntity<UserDto> getDisciplineById(@PathVariable("league_id") String leagueId,
                                                     @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restFacade.getUserByLeagueId(leagueId, user), HttpStatus.OK);
    }
}
