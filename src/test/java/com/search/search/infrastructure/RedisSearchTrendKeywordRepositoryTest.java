package com.search.search.infrastructure;

import com.search.common.constants.SearchConstants;
import com.search.search.dto.TrendKeyword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RedisSearchTrendKeywordRepositoryTest {
    @InjectMocks
    private RedisSearchTrendKeywordRepository trendKeywordRepository;
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateScoreByKeyword() {
        String keyword = "keyword";
        LocalDate date = LocalDate.now();

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        trendKeywordRepository.updateScoreByKeyword(keyword, date);

        verify(zSetOperations, times(1)).addIfAbsent(SearchConstants.REDIS_KEY + date, keyword, 1);
        verify(zSetOperations, times(1)).incrementScore(SearchConstants.REDIS_KEY + date, keyword, 1);
    }

    @Test
    void findTrendKeywordFromRedis() {
        int count = 3;
        LocalDate date = LocalDate.now();

        Set<ZSetOperations.TypedTuple<String>> tuples = new HashSet<>();
        tuples.add(createTypedTuple("keyword1", 10.0));
        tuples.add(createTypedTuple("keyword2", 8.0));
        tuples.add(createTypedTuple("keyword3", 5.0));

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.reverseRangeWithScores(SearchConstants.REDIS_KEY + date, 0, count - 1))
                .thenReturn(tuples);

        List<TrendKeyword> result = trendKeywordRepository.findTopTrendByScoreDesc(count, date);

        assertEquals(3, result.size());

    }

    @Test
    void findTrendKeywordReturnsEmptyList() {
        int count = 3;
        LocalDate date = LocalDate.now();

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.reverseRangeWithScores(SearchConstants.REDIS_KEY + date, 0, count - 1))
                .thenReturn(null);

        List<TrendKeyword> result = trendKeywordRepository.findTopTrendByScoreDesc(count, date);

        assertEquals(0, result.size());
    }

    @Test
    void expireAllScore() {
        LocalDate date = LocalDate.now();

        trendKeywordRepository.expireAllScore(date);

        verify(redisTemplate, times(1))
                .expire(SearchConstants.REDIS_KEY + date, 1L, TimeUnit.HOURS);
    }

    private ZSetOperations.TypedTuple<String> createTypedTuple(String value, Double score) {
        return new ZSetOperations.TypedTuple<String>() {
            @Override
            public String getValue() {
                return value;
            }

            @Override
            public Double getScore() {
                return score;
            }

            @Override
            public int compareTo(ZSetOperations.TypedTuple<String> o) {
                return score.compareTo(o.getScore());
            }
        };
    }
}
