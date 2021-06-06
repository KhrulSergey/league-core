package com.freetonleague.core.controller;

import com.freetonleague.core.config.ApiPageable;
import com.freetonleague.core.domain.dto.NewsDto;
import com.freetonleague.core.domain.enums.NewsStatusType;
import com.freetonleague.core.service.RestNewsFacade;
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
@RequestMapping(path = NewsController.BASE_PATH)
@RequiredArgsConstructor
@Api(value = "News Management Controller")
public class NewsController {

    public static final String BASE_PATH = "/api/news";
    public static final String PATH_CREATE = "/";
    public static final String PATH_EDIT = "/";
    public static final String PATH_GET = "/{news_id}";
    public static final String PATH_DELETE = "/{news_id}";
    public static final String PATH_GET_LIST = "/list";

    private final RestNewsFacade restNewsFacade;

    @ApiOperation("Get news by id")
    @GetMapping(path = PATH_GET)
    public ResponseEntity<NewsDto> getNewsById(@PathVariable("news_id") long id) {
        return new ResponseEntity<>(restNewsFacade.getNews(id), HttpStatus.OK);
    }

    @ApiOperation("Get news list info")
    @ApiPageable
    @GetMapping(path = PATH_GET_LIST)
    public ResponseEntity<Page<NewsDto>> getNewsList(@PageableDefault Pageable pageable,
                                                     @RequestParam(value = "statuses", required = false) NewsStatusType... statuses) {
        List<NewsStatusType> statusList = nonNull(statuses) ? List.of(statuses) : null;
        return new ResponseEntity<>(restNewsFacade.getNewsList(pageable, statusList), HttpStatus.OK);
    }

    @ApiOperation("Create new news on platform")
    @PostMapping(path = PATH_CREATE)
    public ResponseEntity<NewsDto> createNews(@RequestBody NewsDto newsDto) {
        return new ResponseEntity<>(restNewsFacade.addNews(newsDto), HttpStatus.CREATED);
    }

    @ApiOperation("Modify news on platform")
    @PutMapping(path = PATH_EDIT)
    public ResponseEntity<NewsDto> modifyNews(@RequestBody NewsDto newsDto) {
        return new ResponseEntity<>(restNewsFacade.editNews(newsDto), HttpStatus.OK);
    }

    @ApiOperation("Delete (mark) news on platform")
    @DeleteMapping(path = PATH_DELETE)
    public ResponseEntity<NewsDto> deleteNews(@PathVariable("news_id") long id) {
        return new ResponseEntity<>(restNewsFacade.deleteNews(id), HttpStatus.OK);
    }
}
