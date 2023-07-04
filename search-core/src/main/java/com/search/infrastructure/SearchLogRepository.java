package com.search.infrastructure;

import com.search.domain.SearchLog;
import com.search.dto.TrendKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SELECT L.KEYWORD , COUNT(L.id) AS count
 * FROM SEARCH_LOG  L
 * WHERE L.timestamp >= '2023-07-04 00:00:00'
 * GROUP BY L.keyword
 * ORDER BY count DESC
 */

public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {
        @Query("SELECT new com.search.dto.TrendKeyword(L.keyword, COUNT(L.id)) " +
            "FROM SearchLog L WHERE L.timestamp >=?1 " +
            "GROUP BY L.keyword ORDER BY COUNT(L) DESC LIMIT ?2")
    List<TrendKeyword> findTrendKeyword(LocalDateTime date,int count);

}
