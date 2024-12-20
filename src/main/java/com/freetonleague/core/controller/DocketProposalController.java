package com.freetonleague.core.controller;

import com.freetonleague.core.config.ApiPageable;
import com.freetonleague.core.domain.dto.docket.DocketUserProposalBonusDto;
import com.freetonleague.core.domain.dto.docket.DocketUserProposalDto;
import com.freetonleague.core.domain.enums.ParticipationStateType;
import com.freetonleague.core.domain.model.User;
import com.freetonleague.core.service.docket.RestDocketProposalFacade;
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

import java.util.List;

@RestController
@RequestMapping(path = DocketProposalController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Docket Proposal Activity Management Controller")
public class DocketProposalController {

    public static final String BASE_PATH = "/api/docket/proposal";

    public static final String PATH_APPLY_TO_DOCKET = "/apply";
    public static final String PATH_QUIT_FROM_DOCKET = "/quit";
    public static final String PATH_GET_BY_USER_AND_DOCKET = "/";
    public static final String PATH_EDIT_USER_PROPOSAL = "/";
    public static final String PATH_GET_LIST_BY_DOCKET = "/list";
    public static final String PATH_GET_LIST_BY_DOCKET_FOR_BONUS = "/bonus-list";

    private final RestDocketProposalFacade restDocketProposalFacade;

    /**
     * The same value as from "${freetonleague.session.service-token-name}"
     */
    private final String staticServiceTokenName = "access_token";

    @ApiOperation("Get user proposal for docket")
    @GetMapping(path = PATH_GET_BY_USER_AND_DOCKET)
    public ResponseEntity<DocketUserProposalDto> getDocketProposalByUser(@RequestParam(value = "docket_id") long docketId,
                                                                         @RequestParam(value = "league_id") String leagueId) {
        return new ResponseEntity<>(restDocketProposalFacade.getProposalByUserAndDocket(docketId, leagueId), HttpStatus.OK);
    }

    @ApiPageable
    @ApiOperation("Get user proposal list by docket")
    @GetMapping(path = PATH_GET_LIST_BY_DOCKET)
    public ResponseEntity<Page<DocketUserProposalDto>> getDocketProposalList(@PageableDefault Pageable pageable,
                                                                             @RequestParam(value = "docket_id") long docketId) {
        return new ResponseEntity<>(restDocketProposalFacade.getProposalListByDocket(pageable, docketId), HttpStatus.OK);
    }

    @ApiPageable
    @ApiOperation("Get active user proposal list by docket for bonus payments")
    @GetMapping(path = PATH_GET_LIST_BY_DOCKET_FOR_BONUS)
    public ResponseEntity<List<DocketUserProposalBonusDto>> getDocketProposalBonusList(
            @RequestParam(value = staticServiceTokenName, required = false) String token,
            @RequestParam(value = "docket_id") long docketId) {
        return new ResponseEntity<>(restDocketProposalFacade.getProposalListByDocketForBonus(token, docketId), HttpStatus.OK);
    }

    @ApiOperation("Apply to participate in docket by id")
    @PostMapping(path = PATH_APPLY_TO_DOCKET)
    public ResponseEntity<DocketUserProposalDto> applyToDocketById(@RequestBody DocketUserProposalDto userProposalDto,
                                                                   @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restDocketProposalFacade.createProposalToDocket(userProposalDto, user), HttpStatus.OK);
    }

    @ApiOperation("Change user proposal to docket by userProposalId (available edit only state, for orgs)")
    @PutMapping(path = PATH_EDIT_USER_PROPOSAL)
    public ResponseEntity<DocketUserProposalDto> editUserProposal(@RequestParam(value = "user_poposal_id") long userProposalId,
                                                                  @RequestParam(value = "user_poposal_state") ParticipationStateType userProposalState,
                                                                  @ApiIgnore @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(restDocketProposalFacade.editProposalToDocket(userProposalId, userProposalState, user), HttpStatus.OK);
    }

    private ResponseEntity<List<DocketUserProposalBonusDto>> emptyBonusListResponse() {
        return ResponseEntity.noContent().header("Content-Length", "0").build();
    }
}
