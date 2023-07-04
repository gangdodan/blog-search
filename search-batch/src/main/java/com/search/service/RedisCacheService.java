package com.search.service;

import com.search.infrastructure.SearchTrendKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

import static com.common.constants.SearchConstants.TIME_REMOVE_CACHE;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheService {
    private final SearchTrendKeywordRepository keywordRepository;

    @Scheduled(cron = SearchConstants.TIME_REMOVE_CACHE)
    public void removeTrendKeywordCache(){
        keywordRepository.expireAllScore(LocalDate.now().minusDays(1L));
        log.info("Scheduled Task : Removed Trend Keyword Cache.");
    }
}
