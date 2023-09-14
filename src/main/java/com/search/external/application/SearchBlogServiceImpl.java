package com.search.external.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.search.external.dto.KeywordSearchRequest;
import com.search.external.infrastructure.circuit.CircuitBreaker;
import com.search.external.infrastructure.client.CircuitClient;
import com.search.external.infrastructure.searcher.KakaoBlogSearcher;
import com.search.internal.application.SearchEventPublisher;
import com.search.search.domain.SearchLog;
import com.search.search.event.SearchEvent;
import com.search.search.infrastructure.SearchLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.yaml.snakeyaml.util.UriEncoder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.search.common.exception.enums.ErrorCode.REQUEST_CONFLICT;
import static com.search.common.exception.enums.ErrorCode.UNABLE_TO_PROCESS;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchBlogServiceImpl implements SearchOpenApiService {

    private final SearchLogRepository logRepository;
    private final SearchEventPublisher eventPublisher;
    private final CircuitClient circuitClient;


    @Override
    @Transactional
    public Page<JsonNode> requestSearchResult(String keyword, String sort, int page, int size) {
        SearchLog searchLog = saveSearchLog(keyword);
        publishEvent(searchLog);

        KeywordSearchRequest request = KeywordSearchRequest.builder()
                .query(keyword)
                .sort(sort)
                .page(page)
                .size(size)
                .build();
        return circuitClient.search(request);
    }

    public SearchLog saveSearchLog(String keyword) {
        return logRepository.save(SearchLog.builder()
                .keyword(keyword)
                .build());
    }

    //    @Async
//    @Transactional(propagation = Propagation.REQUIRED)
    public void publishEvent(SearchLog searchLog) {
        eventPublisher.publish(SearchEvent.of(searchLog));
    }

}
