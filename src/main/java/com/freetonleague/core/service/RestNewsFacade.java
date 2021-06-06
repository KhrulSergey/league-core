package com.freetonleague.core.service;

import com.freetonleague.core.domain.dto.NewsDto;
import com.freetonleague.core.domain.enums.NewsStatusType;
import com.freetonleague.core.domain.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service-facade for managing news
 */
public interface RestNewsFacade {

    /**
     * Returns founded news by id
     *
     * @param id of news to search
     * @return news entity
     */
    NewsDto getNews(long id);

    /**
     * Returns list of all teams filtered by requested params with detailed info
     *
     * @param pageable   filtered params to search news
     * @param statusList filter params
     * @return list of team entities
     */
    Page<NewsDto> getNewsList(Pageable pageable, List<NewsStatusType> statusList);

    /**
     * Add new news to DB.
     *
     * @param newsDto to be added
     * @return Added news
     */
    NewsDto addNews(NewsDto newsDto);

    /**
     * Edit news in DB.
     *
     * @param newsDto to be edited
     * @return Edited news
     */
    NewsDto editNews(NewsDto newsDto);

    /**
     * Delete news in DB.
     *
     * @param id of news to search
     * @return deleted news
     */
    NewsDto deleteNews(long id);

    /**
     * Getting news by id and user with privacy check
     */
    News getVerifiedNewsById(long id);
}
