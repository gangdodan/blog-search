package com.search.internal.service;

import com.common.constants.SearchConstants;
import com.search.dto.TrendKeyword;
import com.search.infrastructure.SearchLogRepository;
import com.search.infrastructure.SearchTrendKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.common.constants.SearchConstants.RANKING_COUNT;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchTrendKeywordService {
    private final SearchTrendKeywordRepository trendKeywordRepository;
    private final SearchLogRepository logRepository;

    public List<TrendKeyword> getTrendKeywords(LocalDate date) {
        try {
            List<TrendKeyword> results = trendKeywordRepository.findTopTrendByScoreDesc(SearchConstants.RANKING_COUNT, date);
            return !results.isEmpty() ? results : logRepository.findTrendKeyword(date.atTime(LocalTime.MIDNIGHT), SearchConstants.RANKING_COUNT);
        } catch (Exception e) {
            log.error("Failed to get trends from cache. cause by {}", e.getMessage(), e);
            return logRepository.findTrendKeyword(date.atTime(LocalTime.MIDNIGHT), SearchConstants.RANKING_COUNT);
        }
    }
}
