package com.search.controller;

import com.common.dto.PageResponseDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.search.application.SearchOpenApiService;
import com.search.dto.TrendKeyword;
import com.search.service.SearchTrendKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchBlogController {
    private final SearchOpenApiService openApiService;
    private final SearchTrendKeywordService trendKeywordService;

    @GetMapping("/blogs")
    public PageResponseDto<JsonNode> getSearchResultByKakao(
            @RequestParam(required = true,name = "query") String query,
            @RequestParam(required = false,name ="sort") String sort,
            @RequestParam(required = false,name ="page") Integer page,
            @RequestParam(required = false,name ="size") Integer size
    ) {
        Page<JsonNode> results = openApiService.requestSearchResult(query, sort, page, size);
        return PageResponseDto.of(results);
    }

    @GetMapping("/keywords/popular")
    public List<TrendKeyword> getSearchTrendKeyword() {
        return trendKeywordService.getTrendKeywords(LocalDate.now());
    }

}
