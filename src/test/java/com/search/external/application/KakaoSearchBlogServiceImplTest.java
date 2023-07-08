package com.search.external.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.search.search.domain.SearchLog;
import com.search.search.infrastructure.SearchLogRepository;
import com.search.search.infrastructure.SearchTrendKeywordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class KakaoSearchBlogServiceImplTest {
    @InjectMocks
    private KakaoSearchBlogServiceImpl searchService;
    @Mock
    private SearchLogRepository logRepository;
    @Mock
    private SearchTrendKeywordRepository trendKeywordRepository;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private Logger log;
    String apiResponse;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        apiResponse =
                "{ \"meta\": { \"total_count\": 100, \"pageable_count\": 20 }, " +
                "\"documents\": [ { \"id\": 1, \"title\": \"Document 1\" }, " +
                "{ \"id\": 2, \"title\": \"Document 2\" }, { \"id\": 3, \"title\": \"Document 3\" } ] }";

    }

    @Test
    void getDocumentsByPage() throws Exception {
        Page<JsonNode> result = searchService.getDocumentsByPage(apiResponse, 1, 10);

        assertEquals(100, result.getTotalElements());
        assertEquals(10, result.getSize());
        assertEquals(1, result.getNumber());
        assertEquals(10, result.getTotalPages());
        assertTrue(result.hasContent());
        assertTrue(result.hasNext());
        assertTrue(result.hasPrevious());
    }

    @Test
    void getDocumentsByPageReturnNull() {
        apiResponse = "JSON response";
        Page<JsonNode> result = searchService.getDocumentsByPage(apiResponse, 1, 10);

        assertNull(result);
    }

    @Test
    void saveSearchLog() {
        //given
        String keyword = "test";
        SearchLog savedLog = new SearchLog();
        when(logRepository.save(any(SearchLog.class))).thenReturn(savedLog);

        //when
        SearchLog result = searchService.saveSearchLog(keyword);

        //then
        verify(logRepository, times(1)).save(any(SearchLog.class));
        assertEquals(savedLog, result);
    }

//    @Test
//    void buildSearchUri() {
//        String keyword = "test keyword";
//        String sort = "accuracy";
//        int page = 1;
//        int size = 10;
//
//        URI uri = searchService.buildSearchUri(keyword, sort, page, size);
//
//        assertNotNull(uri);
//        String expectedEncodedKeyword = UriEncoder.encode(keyword);
//        String expectedUriString = String.format("%s?query=%s&sort=%s&page=%d&size=%d", uri, expectedEncodedKeyword, sort, page, size);
//        assertEquals(expectedUriString, uri.toString());
//    }
//
//
//    @Test
//    void updateScoreByKeywordWarning() {
//        String keyword = "test";
//        when(logRepository.save(any(SearchLog.class))).thenThrow(new RuntimeException());
//
//        Page<JsonNode> result = searchService.requestSearchResult(keyword, "accuracy", 1, 10);
//
//        assertEquals(null, result);
//    }
//
//    @Test
//    void updateScoreByKeywordSuccess() {
//        URI uri = UriComponentsBuilder.fromHttpUrl("http://test-api-url.com")
//                .queryParam("query", org.yaml.snakeyaml.util.UriEncoder.encode("keyword"))
//                .queryParam("sort", "sort")
//                .queryParam("page", "page")
//                .queryParam("size", "size")
//                .build()
//                .toUri();
//        String keyword = "test";
//        given(searchService.buildSearchUri(keyword, "sort", 1, 10)).willReturn(uri);
//
//        verify(log, never()).warn(anyString());
//    }
//
//
//    @Test
//    void requestSearchResult() {
//        String keyword = "test";
//        String sort = "accuracy";
//        int page = 1;
//        int size = 10;
//        String apiResponse = "{ \"meta\": { \"total_count\": 100, \"pageable_count\": 10 }, \"documents\": [] }";
//        URI expectedUri = URI.create("https://api.kakao.com/v1/search/blog?query=test&sort=accuracy&page=1&size=10");
//
//        HttpHeaders expectedHeaders = new HttpHeaders();
//        expectedHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        expectedHeaders.set("Authorization", "KakaoAK SECRET_KEY");
//
//        ResponseEntity<String> responseEntity = new ResponseEntity<>(apiResponse, HttpStatus.OK);
//
//        SearchLog savedLog = new SearchLog();
//        when(logRepository.save(any(SearchLog.class))).thenReturn(savedLog);
//        when(restTemplate.exchange(eq(expectedUri), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
//                .thenReturn(responseEntity);
//
//        Page<JsonNode> result = searchService.requestSearchResult(keyword, sort, page, size);
//
//        verify(logRepository, times(1)).save(any(SearchLog.class));
//        verify(trendKeywordRepository, times(1)).updateScoreByKeyword(keyword, savedLog.getTimestamp().toLocalDate());
//        verify(restTemplate, times(1)).exchange(eq(expectedUri), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
//
//        assertEquals(0, result.getContent().size());
//        assertEquals(100, result.getTotalElements());
//        assertEquals(10, result.getSize());
//        assertEquals(1, result.getNumber());
//        assertEquals(10, result.getTotalPages());
//        assertEquals(true, result.hasContent());
//        assertEquals(false, result.hasNext());
//        assertEquals(true, result.hasPrevious());
//    }
//
//    @Test
//    void requestSearchResultReturnNull() {
//        String keyword = "test";
//        String sort = "accuracy";
//        int page = 1;
//        int size = 10;
//
//        when(logRepository.save(any(SearchLog.class))).thenThrow(new RuntimeException());
//
//        Page<JsonNode> result = searchService.requestSearchResult(keyword, sort, page, size);
//
//        verify(logRepository, times(1)).save(any(SearchLog.class));
//        verify(trendKeywordRepository, never()).updateScoreByKeyword(anyString(), any());
//        verify(restTemplate, never()).exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), eq(String.class));
//
//        Assertions.assertThat(result).isNotNull();
//    }


}
