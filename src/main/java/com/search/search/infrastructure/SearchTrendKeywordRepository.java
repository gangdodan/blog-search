package com.search.search.infrastructure;

import com.search.search.dto.TrendKeyword;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SearchTrendKeywordRepository{
    void updateScoreByKeyword(String keyword, LocalDate date);
    List<TrendKeyword> findTopTrendByScoreDesc(int count, LocalDate date);
    void expireAllScore(LocalDate date);
}
