package com.search.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.search.application.SearchOpenApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/search/blogs")
@RequiredArgsConstructor
public class SearchBlogController {
    private final SearchOpenApiService openApiService;

    @GetMapping
    public Page<JsonNode> getSearchResultByKakao(
            @RequestParam ("query") String query,
            @RequestParam ("sort") String sort,
            @RequestParam ("page") Integer page,
            @RequestParam ("size") Integer size
    ) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis());
        return openApiService.requestSearchResult(query, sort, page, size,timestamp);
    }

}
