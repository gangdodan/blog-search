package com.search.external.service;

import com.search.common.constants.SearchConstants;
import com.search.search.infrastructure.SearchTrendKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RedisCacheService {
    private final SearchTrendKeywordRepository keywordRepository;

    @Scheduled(cron = SearchConstants.TIME_REMOVE_CACHE)
    public void removeTrendKeywordCache(){
        keywordRepository.expireAllScore(LocalDate.now().minusDays(1L));
        log.info("Scheduled Task : Removed Trend Keyword Cache.");
    }
}
