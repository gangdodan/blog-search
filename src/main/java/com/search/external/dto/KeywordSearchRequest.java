package com.search.external.dto;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class KeywordSearchRequest {
    private final String query;
    private final String sort;
    private final int page;
    private final int size;

}
