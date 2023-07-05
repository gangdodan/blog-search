package com.search.internal.service;

import com.search.common.constants.SearchConstants;
import com.search.search.dto.TrendKeyword;
import com.search.search.infrastructure.SearchLogRepository;
import com.search.search.infrastructure.SearchTrendKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


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
