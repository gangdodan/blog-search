package com.search.external.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.search.search.domain.SearchLog;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoSearchBlogServiceImpl implements SearchOpenApiService {
    @Value("${search.kakao.REST_API_KEY}")
    String secretKey;

    @Value("${search.kakao.URL}")
    String url;
    private final SearchLogRepository logRepository;
    private final SearchTrendKeywordRepository trendKeywordRepository;

    @Override
    @Transactional
    public Page<JsonNode> requestSearchResult(String keyword, String sort, int page, int size) {
        try {
            SearchLog log = saveSearchLog(keyword);
            trendKeywordRepository.updateScoreByKeyword(keyword, log.getTimestamp().toLocalDate());
        } catch (Exception e) {
            log.error("cause by {}",e.getMessage(),e);
//            e.printStackTrace();
        }

        RestTemplate rest = new RestTemplate();
        URI uri = buildSearchUri(keyword, sort, page, size);
        HttpHeaders headers = buildHttpHeaders();

        try {
            ResponseEntity<String> responseEntity = rest.exchange(uri.toString(), HttpMethod.GET, new HttpEntity<>(headers), String.class);
            String responseBody = responseEntity.getBody();
            return getDocumentsByPage(responseBody, page, size);
        } catch (Exception e) {
            log.error("cause by {}",e.getMessage(),e);
            return null;
        }
    }

    private URI buildSearchUri(String keyword, String sort, int page, int size) {
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
            log.error("cause by {}",e.getMessage(),e);
            return null;
        }
    }

    public SearchLog saveSearchLog(String keyword) {
        return logRepository.save(SearchLog.builder()
                .keyword(keyword)
                .build());
    }

}
