package com.search.external.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.search.internal.application.SearchEventPublisher;
import com.search.search.domain.SearchLog;
import com.search.search.event.SearchEvent;
import com.search.search.infrastructure.SearchLogRepository;
import com.search.search.infrastructure.SearchTrendKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
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
@Transactional
public class KakaoSearchBlogServiceImpl implements SearchOpenApiService {
    @Value("${open.kakao.REST_API_KEY}")
    String secretKey;

    @Value("${open.kakao.URL}")
    String url;
    private final SearchLogRepository logRepository;
    private final SearchEventPublisher eventPublisher;

    @Override
    public Page<JsonNode> requestSearchResult(String keyword, String sort, int page, int size) {
        SearchLog searchLog = saveSearchLog(keyword);
        eventPublisher.publish(SearchEvent.of(searchLog));

        RestTemplate rest = new RestTemplate();
        URI uri = buildSearchUri(keyword, sort, page, size);
        HttpHeaders headers = buildHttpHeaders();

        try {
            ResponseEntity<String> responseEntity = rest.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            String responseBody = responseEntity.getBody();
            return getDocumentsByPage(responseBody, page, size);
        } catch (RuntimeException e) {
            log.error(REQUEST_CONFLICT.getStatus() + ": " + REQUEST_CONFLICT.getMessage());
            return null;
        }
    }

    protected URI buildSearchUri(String keyword, String sort, int page, int size) {
        return UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", UriEncoder.encode(keyword))
                .queryParam("sort", sort)
                .queryParam("page", page)
                .queryParam("size", size)
                .build()
                .toUri();
    }

    private HttpHeaders buildHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "KakaoAK " + secretKey);
        return headers;
    }

    public Page<JsonNode> getDocumentsByPage(String apiResponse, int page, int size) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode responseNode = objectMapper.readTree(apiResponse);
            JsonNode documentsNode = responseNode.get("documents");

            int totalCount = responseNode.get("meta").get("total_count").asInt();
            int pageableCount = responseNode.get("meta").get("pageable_count").asInt();

            int startIndex = (page - 1) * size;
            int endIndex = Math.min(startIndex + size, pageableCount);

            List<JsonNode> documents = new ArrayList<>();
            for (int i = startIndex; i < endIndex; i++) {
                documents.add(documentsNode.get(i));
            }

            return new PageImpl<>(documents, PageRequest.of(page, size), totalCount);
        } catch (Exception e) {
            log.error(UNABLE_TO_PROCESS.getStatus() + ": " + UNABLE_TO_PROCESS.getMessage());
            return null;
        }
    }

    public SearchLog saveSearchLog(String keyword) {
        return logRepository.save(SearchLog.builder()
                .keyword(keyword)
                .build());
    }

}
