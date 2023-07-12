package com.search.external.infrastructure.circuit;

import com.search.external.infrastructure.searcher.KakaoBlogSearcher;
import com.search.external.infrastructure.searcher.NaverBlogSearcher;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public class CircuitBreaker<T> {
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;
    private static final int FAILURE_THRESHOLD = 2;


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
