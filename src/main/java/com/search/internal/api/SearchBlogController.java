package com.search.internal.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.search.common.dto.PageResponseDto;
import com.search.external.application.SearchOpenApiService;
import com.search.internal.application.SearchTrendKeywordService;
import com.search.search.dto.TrendKeyword;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/search")
@RestController
@RequiredArgsConstructor
public class SearchBlogController {
    private final SearchOpenApiService openApiService;
    private final SearchTrendKeywordService trendKeywordService;

    @GetMapping("/blogs")
    @ResponseStatus(HttpStatus.OK)
    public PageResponseDto<JsonNode> getSearchResult(
            @RequestParam(required = true, name = "query") String query,
            @RequestParam(required = false, name = "sort") String sort,
            @RequestParam(required = false, name = "page") Integer page,
            @RequestParam(required = false, name = "size") Integer size
    ) {
        Page<JsonNode> results = openApiService.requestSearchResult(query, sort, page, size);
        return PageResponseDto.of(results);
    }

    @GetMapping("/keywords/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<TrendKeyword> getSearchTrendKeyword() {
        return trendKeywordService.getTrendKeywords(LocalDate.now());
    }

}
