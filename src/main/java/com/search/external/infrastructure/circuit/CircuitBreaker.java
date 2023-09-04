package com.search.external.infrastructure.circuit;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public class CircuitBreaker<T> {
    private static final int MAX_RETRIES = 3;
    private static final int FAILURE_THRESHOLD = 2;

    private int retries;
    private boolean circuitOpen;

    public CircuitBreaker() {
        this.retries = 0;
        this.circuitOpen = false;
    }

    /**
     * 요청 시도 분기 처리
     */
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

    /**
     * fallback 처리 -> 네이버 API 요청
     */
    private T makeFallbackApiRequest(Supplier<T> exchangeSupplier) {
        return exchangeSupplier.get();
    }

    /**
     * 재시도 임계 초과 처리 -> circuit 상태 open
     */
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
