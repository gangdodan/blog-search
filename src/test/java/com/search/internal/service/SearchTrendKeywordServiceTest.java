package com.search.internal.service;

import com.search.common.constants.SearchConstants;
import com.search.search.dto.TrendKeyword;
import com.search.search.infrastructure.SearchLogRepository;
import com.search.search.infrastructure.SearchTrendKeywordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SearchTrendKeywordServiceTest {
    @InjectMocks
    private SearchTrendKeywordService searchTrendKeywordService;
    @Mock
    private SearchTrendKeywordRepository trendKeywordRepository;
    @Mock
    private SearchLogRepository logRepository;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("정상 동작_레디스에서 조회")
    @Test
    void getTrendKeywordsFromRedis() {
        LocalDate date = LocalDate.now();
        List<TrendKeyword> expectedKeywords = Collections.singletonList(TrendKeyword.of("keyword", 10L));

        when(trendKeywordRepository.findTopTrendByScoreDesc(SearchConstants.RANKING_COUNT, date))
                .thenReturn(expectedKeywords);

        List<TrendKeyword> result = searchTrendKeywordService.getTrendKeywords(date);

        assertEquals(expectedKeywords, result);
        verify(logRepository, never()).findTrendKeyword(any(LocalDateTime.class), eq(SearchConstants.RANKING_COUNT));
    }

    @DisplayName("예외 동작_레디스에서 빈 값 조회 시, DB에서 조회")
    @Test()
    void getTrendKeywordsFromLogRepository() {
        LocalDate date = LocalDate.now();
        List<TrendKeyword> expectedKeywords = Collections.singletonList(TrendKeyword.of("keyword", 10L));

        when(trendKeywordRepository.findTopTrendByScoreDesc(SearchConstants.RANKING_COUNT, date))
                .thenReturn(Collections.emptyList());
        when(logRepository.findTrendKeyword(any(LocalDateTime.class), eq(SearchConstants.RANKING_COUNT)))
                .thenReturn(expectedKeywords);

        List<TrendKeyword> result = searchTrendKeywordService.getTrendKeywords(date);

        assertEquals(expectedKeywords, result);
        verify(logRepository, times(1)).findTrendKeyword(any(LocalDateTime.class), eq(SearchConstants.RANKING_COUNT));
    }

    @DisplayName("예외 동작_레디스 조회 실패 시, DB에서 조회")
    @Test()
    void getTrendKeywordsFromLogRepositoryByException() {
        LocalDate date = LocalDate.now();
        List<TrendKeyword> expectedKeywords = Collections.singletonList(TrendKeyword.of("keyword", 10L));

        when(trendKeywordRepository.findTopTrendByScoreDesc(SearchConstants.RANKING_COUNT, date))
                .thenThrow(new RuntimeException());
        when(logRepository.findTrendKeyword(any(LocalDateTime.class), eq(SearchConstants.RANKING_COUNT)))
                .thenReturn(expectedKeywords);

        List<TrendKeyword> result = searchTrendKeywordService.getTrendKeywords(date);

        assertEquals(expectedKeywords, result);
        verify(logRepository, times(1)).findTrendKeyword(any(LocalDateTime.class), eq(SearchConstants.RANKING_COUNT));
    }
}
