package com.freetonleague.core.controller;

import com.freetonleague.core.config.ApiPageable;
import com.freetonleague.core.domain.dto.docket.DocketDto;
import com.freetonleague.core.domain.enums.docket.DocketStatusType;
import com.freetonleague.core.service.docket.RestDocketFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Objects.nonNull;

@RestController
@RequestMapping(path = DocketController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "Docket Management Controller")
public class DocketController {

    public static final String BASE_PATH = "/api/docket";
    public static final String PATH_CREATE = "/";
    public static final String PATH_EDIT = "/";
    public static final String PATH_GET = "/{docket_id}";
    public static final String PATH_DELETE = "/{docket_id}";
    public static final String PATH_GET_LIST = "/list";

    private final RestDocketFacade restDocketFacade;

    @ApiOperation("Get docket by id")
    @GetMapping(path = PATH_GET)
    public ResponseEntity<DocketDto> getDocketById(@PathVariable("docket_id") long id) {
        return new ResponseEntity<>(restDocketFacade.getDocket(id), HttpStatus.OK);
    }

    @ApiOperation("Get docket list info")
    @ApiPageable
    @GetMapping(path = PATH_GET_LIST)
    public ResponseEntity<Page<DocketDto>> getDocketList(@PageableDefault Pageable pageable,
                                                         @RequestParam(value = "creator", required = false) String creatorLeagueId,
                                                         @RequestParam(value = "statuses", required = false) DocketStatusType... statuses) {
        List<DocketStatusType> statusList = nonNull(statuses) ? List.of(statuses) : null;
        return new ResponseEntity<>(restDocketFacade.getDocketList(pageable, creatorLeagueId, statusList), HttpStatus.OK);
    }

    @ApiOperation("Create new docket on platform")
    @PostMapping(path = PATH_CREATE)
    public ResponseEntity<DocketDto> createDocket(@RequestBody DocketDto docketDto) {
        return new ResponseEntity<>(restDocketFacade.addDocket(docketDto), HttpStatus.CREATED);
    }

    @ApiOperation("Modify docket on platform")
    @PutMapping(path = PATH_EDIT)
    public ResponseEntity<DocketDto> modifyDocket(@RequestBody DocketDto docketDto) {
        return new ResponseEntity<>(restDocketFacade.editDocket(docketDto), HttpStatus.OK);
    }

    @ApiOperation("Delete (mark) docket on platform")
    @DeleteMapping(path = PATH_DELETE)
    public ResponseEntity<DocketDto> deleteDocket(@PathVariable("docket_id") long id) {
        return new ResponseEntity<>(restDocketFacade.deleteDocket(id), HttpStatus.OK);
    }
}
