package com.search.internal.application;

import com.search.common.constants.SearchConstants;
import com.search.search.dto.TrendKeyword;
import com.search.search.infrastructure.SearchLogRepository;
import com.search.search.infrastructure.SearchTrendKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import static com.search.common.exception.enums.ErrorCode.FAILED_TO_GET_TREND_KEYWORD;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchTrendKeywordService {
    private final SearchTrendKeywordRepository trendKeywordRepository;
    private final SearchLogRepository logRepository;

    public List<TrendKeyword> getTrendKeywords(LocalDate date) {
        try {
            List<TrendKeyword> results = trendKeywordRepository.findTopTrendByScoreDesc(SearchConstants.RANKING_COUNT, date);
            return !results.isEmpty() ? results : logRepository.findTrendKeyword(date.atTime(LocalTime.MIDNIGHT), SearchConstants.RANKING_COUNT);
        } catch (Exception e) {
            log.error(FAILED_TO_GET_TREND_KEYWORD.getStatus() + ": " + FAILED_TO_GET_TREND_KEYWORD.getMessage());
            return logRepository.findTrendKeyword(date.atTime(LocalTime.MIDNIGHT), SearchConstants.RANKING_COUNT);
        }
    }
}
