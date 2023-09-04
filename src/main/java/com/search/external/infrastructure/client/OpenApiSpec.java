package com.search.external.infrastructure.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.search.external.dto.KeywordSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;

import java.net.URI;

public interface OpenApiSpec {
    Page<JsonNode> search(KeywordSearchRequest request);

    URI buildSearchUri(KeywordSearchRequest request);

    HttpHeaders buildHttpHeaders();

}
