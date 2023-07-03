package com.search.application;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;

public interface SearchOpenApiService {
    public Page<JsonNode> requestSearchResult(String query, String sort, int page, int size, String timestamp);
}
