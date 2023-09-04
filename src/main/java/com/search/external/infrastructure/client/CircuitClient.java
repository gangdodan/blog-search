package com.search.external.infrastructure.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.search.external.dto.KeywordSearchRequest;
import com.search.external.infrastructure.circuit.CircuitBreaker;
import com.search.external.infrastructure.searcher.KakaoBlogSearcher;
import com.search.external.infrastructure.searcher.NaverBlogSearcher;
import org.springframework.data.domain.Page;

public class CircuitClient extends CircuitBreaker<Page<JsonNode>> {
    private final KakaoBlogSearcher kakaoBlogSearcher;
    private final NaverBlogSearcher naverBlogSearcher;

    public CircuitClient(KakaoBlogSearcher kakaoBlogSearcher, NaverBlogSearcher naverBlogSearcher) {
        this.kakaoBlogSearcher = kakaoBlogSearcher;
        this.naverBlogSearcher = naverBlogSearcher;
    }


    public Page<JsonNode> search(KeywordSearchRequest request) {
        return executeWithRetry(() -> kakaoBlogSearcher.search(request), () -> naverBlogSearcher.search(request));
    }


}
