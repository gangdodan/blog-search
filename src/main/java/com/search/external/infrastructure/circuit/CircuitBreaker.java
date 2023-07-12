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

@Slf4j
public class CircuitBreaker {
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;
    private static final int FAILURE_THRESHOLD = 2;
//    private static final String API_KAKAO = "kakao";
//    private static final String API_NAVER = "naver";

    private final KakaoBlogSearcher kakaoBlogSearcher;
    private final NaverBlogSearcher naverBlogSearcher;

    private int retries;
    private boolean circuitOpen;

    public CircuitBreaker(KakaoBlogSearcher kakaoBlogSearcher,NaverBlogSearcher naverBlogSearcher) {
        this.kakaoBlogSearcher = kakaoBlogSearcher;
        this.naverBlogSearcher = naverBlogSearcher;
        this.retries = 0;
        this.circuitOpen = false;
    }

    public Page<JsonNode> executeWithRetry(KeywordSearchRequest request){
        while (retries < MAX_RETRIES) {

            try {
                if (!circuitOpen) {
                    return kakaoBlogSearcher.search(request);
                } else {
                    return makeFallbackApiRequest(request);
                }
            } catch (RuntimeException e) {
                retries++;
                if (retries >= MAX_RETRIES) {
                    handleRetryExhausted();
                }
            }
        }
        return null;
    }

//    private Page<JsonNode> requestApi(KeywordSearchRequest request){
//        //api 요청 수행
//        blogSearcher.search();
//
//    }

    private Page<JsonNode> makeFallbackApiRequest(KeywordSearchRequest request) {
        //대체 api 요청 수행
        //네이버로 교체_파라미터로 바꿔야 하나? 서비스 로직을 쪼개야 하나?
//        try {
//            Method method = getClass().getDeclaredMethod("requestApi",String.class);
//            method.invoke(this,vendor);
//        } catch (Exception e) {
//            throw new CustomException(ErrorCode.DATA_NOT_FOUND);
//        }
        return naverBlogSearcher.search(request);


    }

    private void handleRetryExhausted(){
        log.warn("Retries exhausted, API replaced. failureTime = {}", System.currentTimeMillis());
        if (retries >= FAILURE_THRESHOLD) {
            log.info("CircuitBreaker changed state to OPEN");
            circuitOpen = true;
        }
    }


}
