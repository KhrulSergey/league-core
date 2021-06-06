package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.dto.NewsDto;
import com.freetonleague.core.domain.enums.NewsStatusType;
import com.freetonleague.core.domain.model.News;
import com.freetonleague.core.exception.ExceptionMessages;
import com.freetonleague.core.exception.NewsManageException;
import com.freetonleague.core.exception.ValidationException;
import com.freetonleague.core.mapper.NewsMapper;
import com.freetonleague.core.security.permissions.CanManageNews;
import com.freetonleague.core.service.NewsService;
import com.freetonleague.core.service.RestNewsFacade;
import com.freetonleague.core.service.RestUserFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class RestNewsFacadeImpl implements RestNewsFacade {

    private final NewsService newsService;
    private final NewsMapper newsMapper;
    private final RestUserFacade restUserFacade;
    private final Validator validator;

    /**
     * Returns founded news by id
     */
    @Override
    public NewsDto getNews(long id) {
        return newsMapper.toDto(this.getVerifiedNewsById(id));
    }

    /**
     * Returns list of all teams filtered by requested params with detailed info
     */
    @Override
    public Page<NewsDto> getNewsList(Pageable pageable, List<NewsStatusType> statusList) {
        return newsService.getNewsList(pageable, statusList).map(newsMapper::toDto);
    }

    /**
     * Add new news to DB.
     */
    @CanManageNews
    @Override
    public NewsDto addNews(NewsDto newsDto) {
        newsDto.setId(null);
        newsDto.setStatus(NewsStatusType.ACTIVE);

        News news = this.getVerifiedNewsByDto(newsDto);
        news = newsService.addNews(news);

        if (isNull(news)) {
            log.error("!> error while creating news from dto '{}'.", newsDto);
            throw new NewsManageException(ExceptionMessages.NEWS_CREATION_ERROR,
                    "News was not saved on Portal. Check requested params.");
        }
        return newsMapper.toDto(news);
    }

    /**
     * Edit news in DB.
     */
    @CanManageNews
    @Override
    public NewsDto editNews(NewsDto newsDto) {
        News modifiedNews = this.getVerifiedNewsByDto(newsDto);
        if (isNull(newsDto.getId())) {
            log.warn("~ parameter 'news.id' is not set for editNews");
            throw new ValidationException(ExceptionMessages.NEWS_VALIDATION_ERROR, "news id",
                    "parameter 'news id' is not set for editNews");
        }
        if (newsDto.getStatus().isDeleted()) {
            log.warn("~ news deleting was declined in editNews. This operation should be done with specific method.");
            throw new NewsManageException(ExceptionMessages.NEWS_STATUS_DELETE_ERROR,
                    "Modifying news was rejected. Check requested params and method.");
        }
        //verify existence and status of existed news
        this.getVerifiedNewsById(newsDto.getId());
        //edit news
        modifiedNews = newsService.editNews(modifiedNews);
        if (isNull(modifiedNews)) {
            log.error("!> error while modifying Media resource from dto '{}'.", newsDto);
            throw new NewsManageException(ExceptionMessages.NEWS_MODIFICATION_ERROR,
                    "Media resource was not updated on Portal. Check requested params.");
        }
        return newsMapper.toDto(modifiedNews);
    }

    /**
     * Delete news in DB.
     */
    @CanManageNews
    @Override
    public NewsDto deleteNews(long id) {
        News news = this.getVerifiedNewsById(id);
        news = newsService.deleteNews(news);
        if (isNull(news)) {
            log.error("!> error while deleting news with id '{}'.", id);
            throw new NewsManageException(ExceptionMessages.NEWS_MODIFICATION_ERROR,
                    "News was not deleted on Portal. Check requested params.");
        }
        return newsMapper.toDto(news);
    }

    /**
     * Getting news by id and user with privacy check
     */
    @Override
    public News getVerifiedNewsById(long id) {
        News news = newsService.getNews(id);
        if (isNull(news)) {
            log.debug("^ News with requested id '{}' was not found. 'getVerifiedNewsById' in RestNewsFacadeImpl request denied", id);
            throw new NewsManageException(ExceptionMessages.NEWS_NOT_FOUND_ERROR, "Media resource with requested id " + id + " was not found");
        }
        if (news.getStatus().isDeleted()) {
            log.debug("^ News with requested id '{}' was '{}'. 'getVerifiedNewsById' in RestNewsFacadeImpl request denied", id, news.getStatus());
            throw new NewsManageException(ExceptionMessages.NEWS_VISIBLE_ERROR, "Visible news with requested id " + id + " was not found");
        }
        return news;
    }

    /**
     * Getting news by DTO with deep validation and privacy check
     */
    private News getVerifiedNewsByDto(NewsDto newsDto) {
        // Verify News information
        Set<ConstraintViolation<NewsDto>> violations = validator.validate(newsDto);
        if (!violations.isEmpty()) {
            log.debug("^ transmitted NewsDto: '{}' have constraint violations: '{}'", newsDto, violations);
            throw new ConstraintViolationException(violations);
        }
        return newsMapper.fromDto(newsDto);
    }
}
