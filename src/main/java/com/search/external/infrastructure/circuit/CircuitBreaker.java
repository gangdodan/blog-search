package com.search.external.infrastructure.circuit;

import com.fasterxml.jackson.databind.JsonNode;
import com.search.common.exception.domain.CustomException;
import com.search.common.exception.enums.ErrorCode;
import com.search.external.application.SearchBlogServiceImpl;
import com.search.external.dto.KeywordSearchRequest;
import com.search.external.infrastructure.searcher.KakaoBlogSearcher;
import com.search.external.infrastructure.searcher.NaverBlogSearcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

@Slf4j
public class CircuitBreaker<T> {
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;
    private static final int FAILURE_THRESHOLD = 2;
//    private static final String API_KAKAO = "kakao";
//    private static final String API_NAVER = "naver";

    private final KakaoBlogSearcher kakaoBlogSearcher;
    private final NaverBlogSearcher naverBlogSearcher;

    private int retries;
    private boolean circuitOpen;

    public CircuitBreaker(KakaoBlogSearcher kakaoBlogSearcher, NaverBlogSearcher naverBlogSearcher) {
        this.kakaoBlogSearcher = kakaoBlogSearcher;
        this.naverBlogSearcher = naverBlogSearcher;
        this.retries = 0;
        this.circuitOpen = false;
    }

    public T executeWithRetry(Supplier<T> supplier, Supplier<T> exchangeSupplier) {
        while (retries < MAX_RETRIES) {
            try {
                if (!circuitOpen) {
                    return supplier.get();
                }
            } catch (RuntimeException e) {
                retries++;
                if (retries >= MAX_RETRIES) {
                    handleRetryExhausted();
                    return makeFallbackApiRequest(exchangeSupplier);
                }
            }
        }
        return null;
    }


    private T makeFallbackApiRequest(Supplier<T> exchangeSupplier) {
        return exchangeSupplier.get();
    }

    private void handleRetryExhausted() {
        log.warn("Retries exhausted, API replaced. failureTime = {}", System.currentTimeMillis());
        if (retries >= FAILURE_THRESHOLD) {
            log.info("CircuitBreaker changed state to OPEN");
            circuitOpen = true;
            reset();
        }
    }

    private void reset() {
        retries = 0;
        log.info("CircuitBreaker changed state to CLOSE");
    }

}
