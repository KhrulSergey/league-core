package com.freetonleague.core.controller;

import com.freetonleague.core.domain.dto.UserDto;
import com.freetonleague.core.domain.dto.UserPublicDto;
import com.freetonleague.core.domain.filter.UserInfoFilter;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.RestUserFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.String.format;

@RestController
@RequestMapping(path = UserController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "User-Core Management Controller")
public class UserController {

    public static final String BASE_PATH = "/api/user";
    public static final String PATH_GET = "/{league_id}";
    public static final String PATH_IMPORT_DATA = "/import";
    /**
     * The same value as from "${freetonleague.session.service-token-name}"
     */
    private final String staticServiceTokenName = "access_token";


    private final RestUserFacade restFacade;

    @ApiOperation("Get user info by league id")
    @GetMapping(path = PATH_GET)
    public ResponseEntity<UserPublicDto> getUserByLeagueId(@PathVariable("league_id") String leagueId,
                                                           @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restFacade.getUserByLeagueId(leagueId, user), HttpStatus.OK);
    }

    @ApiOperation("Update user self info")
    @PutMapping
    public ResponseEntity<UserDto> updateSelfInfo(
            @Valid @RequestBody UserInfoFilter filter,
            @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restFacade.updateUserInfoByFilter(filter, user), HttpStatus.OK);
    }

    @ApiOperation("Import users from file (only for admin)")
    @GetMapping(path = PATH_IMPORT_DATA)
    public void importUsersDataFromFile(HttpServletResponse response) throws IOException {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String dateTime = LocalDateTime.now().format(formatter);
        String fileName = format("user_export_%s.csv", dateTime);
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.setContentType("text/csv");
        restFacade.importUsersDataFromFile(response.getOutputStream());
    }
}
