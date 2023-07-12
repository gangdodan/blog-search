package com.search.external.infrastructure.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.search.external.dto.KeywordSearchRequest;
import org.springframework.data.domain.Page;

public interface KakaoApiSpec {
    Page<JsonNode> search(KeywordSearchRequest request);
}
