package com.search.search.infrastructure;

import com.search.common.constants.SearchConstants;
import com.search.search.dto.TrendKeyword;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RedisSearchTrendKeywordRepository implements SearchTrendKeywordRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void updateScoreByKeyword(String keyword, LocalDate date) {
        redisTemplate.opsForZSet().incrementScore(SearchConstants.REDIS_KEY + date, keyword, 1);
    }

    @Override
    public List<TrendKeyword> findTopTrendByScoreDesc(int count, LocalDate date) {
        Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate
                .opsForZSet()
                .reverseRangeWithScores(SearchConstants.REDIS_KEY + date, 0, count - 1);
        if (Objects.isNull(tuples)) {
            return Collections.emptyList();
        }

        return tuples.stream()
                .map(tuple -> TrendKeyword.of(tuple.getValue(), tuple.getScore().longValue()))
                .collect(Collectors.toList());
    }

    @Override
    public void expireAllScore(LocalDate date) {
        redisTemplate.expire(SearchConstants.REDIS_KEY + date, 1L, TimeUnit.HOURS);
    }
}
