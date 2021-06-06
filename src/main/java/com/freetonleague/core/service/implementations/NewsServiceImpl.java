package com.freetonleague.core.service.implementations;

import com.freetonleague.core.domain.enums.NewsStatusType;
import com.freetonleague.core.domain.model.News;
import com.freetonleague.core.repository.NewsRepository;
import com.freetonleague.core.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Slf4j
@RequiredArgsConstructor
@Service
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final Validator validator;

    /**
     * Returns founded news by id
     */
    @Override
    public News getNews(long newsId) {
        log.debug("^ trying to get news by id: '{}'", newsId);
        return newsRepository.findById(newsId).orElse(null);
    }

    /**
     * Returns list of all newss filtered by requested params
     */
    @Override
    public Page<News> getNewsList(Pageable pageable, List<NewsStatusType> statusList) {
        if (isNull(pageable)) {
            log.error("!> requesting getNewsList for NULL pageable. Check evoking clients");
            return null;
        }
        log.debug("^ trying to get news list with pageable params: '{}' and status list '{}'", pageable, statusList);
        boolean filterByStatusEnabled = isNotEmpty(statusList);

        if (filterByStatusEnabled) {
            return newsRepository.findAllByStatusIn(pageable, statusList);
        }
        return newsRepository.findAllNews(pageable);
    }

    /**
     * Add new News to DB.
     */
    @Override
    public News addNews(News news) {
        if (!this.verifyNews(news)) {
            return null;
        }
        log.debug("^ trying to add new news '{}'", news);
        return newsRepository.save(news);
    }

    /**
     * Edit news in DB.
     */
    @Override
    public News editNews(News news) {
        if (!this.verifyNews(news)) {
            return null;
        }
        if (!this.isExistsNewsById(news.getId())) {
            log.error("!> requesting modify news.id '{}' and title '{}' for non-existed news. Check evoking clients", news.getId(), news.getTitle());
            return null;
        }
        log.debug("^ trying to modify news '{}'", news);
        if (news.isStatusChanged()) {
            this.handleNewsStatusChanged(news);
        }
        return newsRepository.save(news);
    }

    /**
     * Mark 'deleted' news in DB.
     */
    @Override
    public News deleteNews(News news) {
        if (!this.verifyNews(news)) {
            return null;
        }
        if (!this.isExistsNewsById(news.getId())) {
            log.error("!> requesting delete news for non-existed news. Check evoking clients");
            return null;
        }
        log.debug("^ trying to set 'deleted' mark to news '{}'", news);
        news.setStatus(NewsStatusType.DELETED);
        news = newsRepository.save(news);
        this.handleNewsStatusChanged(news);
        return news;
    }

    /**
     * Returns sign of news existence for specified id.
     */
    @Override
    public boolean isExistsNewsById(long id) {
        return newsRepository.existsById(id);
    }

    /**
     * Validate news parameters and settings to modify
     */
    private boolean verifyNews(News news) {
        if (isNull(news)) {
            log.error("!> requesting modify news with verifyNews for NULL news. Check evoking clients");
            return false;
        }
        Set<ConstraintViolation<News>> violations = validator.validate(news);
        if (!violations.isEmpty()) {
            log.error("!> requesting modify news id '{}' title '{}' with verifyNews for news with ConstraintViolations. Check evoking clients",
                    news.getId(), news.getTitle());
            return false;
        }
        return true;
    }

    /**
     * Prototype for handle news status
     */
    private void handleNewsStatusChanged(News news) {
        log.warn("~ status for news id '{}' was changed from '{}' to '{}' ",
                news.getId(), news.getPrevStatus(), news.getStatus());
        news.setPrevStatus(news.getStatus());
    }
}
