package com.search.external.infrastructure.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.search.external.dto.KeywordSearchRequest;
import com.search.external.infrastructure.circuit.CircuitBreaker;
import com.search.external.infrastructure.searcher.KakaoBlogSearcher;
import com.search.external.infrastructure.searcher.NaverBlogSearcher;
import org.springframework.data.domain.Page;

public class KakaoCircuitClient extends CircuitBreaker<Page<JsonNode>> {
    private final KakaoBlogSearcher kakaoBlogSearcher;
    private final NaverBlogSearcher naverBlogSearcher;

    public KakaoCircuitClient(KakaoBlogSearcher kakaoBlogSearcher, NaverBlogSearcher naverBlogSearcher, NaverBlogSearcher naverBlogSearcher1) {
        super(kakaoBlogSearcher, naverBlogSearcher);
        this.kakaoBlogSearcher = kakaoBlogSearcher;
        this.naverBlogSearcher = naverBlogSearcher1;
    }


    public Page<JsonNode> search(KeywordSearchRequest request) {
        return executeWithRetry(() -> kakaoBlogSearcher.search(request), () -> naverBlogSearcher.search(request));
    }


}
