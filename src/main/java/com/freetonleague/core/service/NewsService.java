package com.freetonleague.core.service;


import com.freetonleague.core.domain.enums.NewsStatusType;
import com.freetonleague.core.domain.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NewsService {

    /**
     * Returns News by identifier from DB.
     *
     * @param newsId News's leagueID to search
     * @return News with a specific identifier, null - if the News is not found.
     */
    News getNews(long newsId);

    /**
     * Returns list of all teams filtered by requested params with detailed info
     *
     * @param pageable   filtered params to search news
     * @param statusList filter params
     * @return News entity, null - if the News is not found.
     */
    Page<News> getNewsList(Pageable pageable, List<NewsStatusType> statusList);

    /**
     * Add new News to DB.
     *
     * @param news to be added
     * @return Added News
     */
    News addNews(News news);

    /**
     * Edit news in DB.
     *
     * @param news to be edited
     * @return Edited news
     */
    News editNews(News news);

    /**
     * Mark 'deleted' news in DB.
     *
     * @param news to be deleted
     * @return news with updated fields and deleted status
     */
    News deleteNews(News news);

    /**
     * Returns sign of news existence for specified id.
     *
     * @param id for which news will be find
     * @return true if news exists, false - if not
     */
    boolean isExistsNewsById(long id);
}
