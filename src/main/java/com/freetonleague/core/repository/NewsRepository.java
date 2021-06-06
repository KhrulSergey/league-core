package com.freetonleague.core.repository;

import com.freetonleague.core.domain.enums.NewsStatusType;
import com.freetonleague.core.domain.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long>,
        JpaSpecificationExecutor<News> {

    @Query(value = "select n from News n where n.status <> com.freetonleague.core.domain.enums.TeamStateType.DELETED")
    Page<News> findAllNews(Pageable pageable);

    Page<News> findAllByStatusIn(Pageable pageable, List<NewsStatusType> statusList);
}
