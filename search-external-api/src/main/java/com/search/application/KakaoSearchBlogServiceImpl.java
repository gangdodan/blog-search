package com.search.application;

import com.search.domain.SearchLog;
import com.search.infrastructure.SearchLogRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.search.infrastructure.SearchTrendKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KakaoSearchBlogServiceImpl implements SearchOpenApiService {
    @Value("${kakao.secretKey}")
    String secretKey;
    private final SearchLogRepository logRepository;
    private final SearchEventPublisher searchEventPublisher;
    private final SearchTrendKeywordRepository trendKeywordRepository;
    private final RedisTemplate<String,String> redisTemplate;

    @Override
    @Transactional
    public Page<JsonNode> requestSearchResult(String keyword, String sort, int page, int size,String timestamp) {
        try {
            saveSearchLog(keyword,timestamp);
            trendKeywordRepository.updateScoreByKeyword(keyword,timestamp);

        } catch (Exception e) {
            throw new RuntimeException("");
        }
        RestTemplate rest = new RestTemplate();
        URI uri = UriComponentsBuilder.fromHttpUrl("https://dapi.kakao.com/v2/search/blog")
                .queryParam("query", keyword)
                .queryParam("sort", sort)
                .queryParam("page", page)
                .queryParam("size", size)
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "KakaoAK " + "168f49791c65ed92825014082de464c4");

        try {
            ResponseEntity<String> responseEntity = rest.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            String responseBody = responseEntity.getBody();
            return getDocumentsByPage(responseBody, page, size);
        } catch (Exception e) {
            throw new RuntimeException("");
        }


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
            e.printStackTrace();
            return null;
        }
    }

    public void saveSearchLog(String keyword,String timestamp) {
        logRepository.save(SearchLog.builder()
                .keyword(keyword)
                        .timestamp(timestamp)
                .build());
    }

}
