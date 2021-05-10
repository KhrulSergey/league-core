package com.freetonleague.core.controller;

import com.freetonleague.core.config.ApiPageable;
import com.freetonleague.core.domain.dto.DocketUserProposalDto;
import com.freetonleague.core.domain.enums.ParticipationStateType;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.RestDocketProposalFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(path = DocketProposalController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Docket Activity From Team Management Controller")
public class DocketProposalController {

    public static final String BASE_PATH = "/api/docket/proposal";

    public static final String PATH_APPLY_TO_DOCKET = "/apply";
    public static final String PATH_QUIT_FROM_DOCKET = "/quit";
    public static final String PATH_GET_FOR_DOCKET = "/";
    public static final String PATH_EDIT_USER_PROPOSAL = "/";
    public static final String PATH_GET_LIST_FOR_DOCKET = "/list";

    private final RestDocketProposalFacade restDocketProposalFacade;

    @ApiOperation("Get user proposal for docket")
    @GetMapping(path = PATH_GET_FOR_DOCKET)
    public ResponseEntity<DocketUserProposalDto> getDocketProposalByUser(@RequestParam(value = "docket_id", required = true) long docketId,
                                                                         @RequestParam(value = "league_id", required = true) String leagueId) {
        return new ResponseEntity<>(restDocketProposalFacade.getProposalFromUserForDocket(docketId, leagueId), HttpStatus.OK);
    }

    @ApiPageable
    @ApiOperation("Get user proposal list for docket")
    @GetMapping(path = PATH_GET_LIST_FOR_DOCKET)
    public ResponseEntity<Page<DocketUserProposalDto>> getDocketProposalList(@PageableDefault Pageable pageable,
                                                                             @RequestParam(value = "docket_id", required = true) long docketId) {

        return new ResponseEntity<>(restDocketProposalFacade.getProposalListForDocket(pageable, docketId), HttpStatus.OK);
    }

    @ApiOperation("Apply to participate in docket by id")
    @PostMapping(path = PATH_APPLY_TO_DOCKET)
    public ResponseEntity<DocketUserProposalDto> applyToDocketById(@RequestBody(required = true) DocketUserProposalDto userProposalDto,
                                                                   @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restDocketProposalFacade.createProposalToDocket(userProposalDto, user), HttpStatus.OK);
    }

    @ApiOperation("Change user proposal to docket by userProposalId or by docketId + leagueId (available edit only state, for orgs)")
    @PutMapping(path = PATH_EDIT_USER_PROPOSAL)
    public ResponseEntity<DocketUserProposalDto> editUserProposal(@RequestParam(value = "docket_id", required = false) Long docketId,
                                                                  @RequestParam(value = "league_id", required = false) String leagueId,
                                                                  @RequestParam(value = "user_poposal_id", required = false) Long userProposalId,
                                                                  @RequestParam(value = "user_poposal_state", required = true) ParticipationStateType userProposalState,
                                                                  @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restDocketProposalFacade.editProposalToDocket(docketId, leagueId, userProposalId, userProposalState, user), HttpStatus.OK);
    }

//    @ApiOperation("Quit user from docket by docket and user id")
//    @PostMapping(path = PATH_QUIT_FROM_DOCKET)
//    public ResponseEntity<Void> quitFromDocketById(@RequestParam(value = "docket_id", required = true) long docketId,
//                                                       @RequestParam(value = "leagueId", required = true) long leagueId,
//                                                       @ApiIgnore @AuthenticationPrincipal User user) {
//        restDocketProposalFacade.quitFromDocket(docketId, leagueId, user);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }


}
