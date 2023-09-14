package com.search.external.infrastructure.searcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.search.external.dto.KeywordSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.yaml.snakeyaml.util.UriEncoder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.search.common.exception.enums.ErrorCode.REQUEST_CONFLICT;
import static com.search.common.exception.enums.ErrorCode.UNABLE_TO_PROCESS;

@Slf4j
@Component
public class NaverBlogSearcher {

    @Value("${open.naver.CLIENT_ID}")
    String clientId;
    @Value("${open.naver.REST_API_KEY}")
    String secretKey;

    @Value("${open.naver.URL}")
    String url;


    public Page<JsonNode> search(KeywordSearchRequest request) {
        RestTemplate rest = new RestTemplate();
        URI uri = buildSearchUri(request);
        HttpHeaders headers = buildHttpHeaders();

        try {
            ResponseEntity<String> responseEntity = rest.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            String responseBody = responseEntity.getBody();
            return getDocumentsByPage(responseBody, request.getPage(), request.getSize());
        } catch (RuntimeException e) {
            log.error(REQUEST_CONFLICT.getStatus() + ": " + REQUEST_CONFLICT.getMessage());
            return null;
        }
    }

    protected URI buildSearchUri(KeywordSearchRequest request) {
        return UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", UriEncoder.encode(request.getQuery()))
                .queryParam("display", request.getSize())
                .queryParam("start", request.getPage())
                .queryParam("sort", request.getSort())
                .build()
                .toUri();
    }

    private HttpHeaders buildHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", secretKey);
        return headers;
    }

    public Page<JsonNode> getDocumentsByPage(String apiResponse, int page, int size) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode responseNode = objectMapper.readTree(apiResponse);
            JsonNode documentsNode = responseNode.get("items");

            int totalCount = responseNode.get("total").asInt();
            int pageableCount = responseNode.get("start").asInt();

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


}
