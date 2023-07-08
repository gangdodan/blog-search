package com.search.internal.application;

import com.search.search.domain.SearchLog;
import com.search.search.event.SearchEvent;
import com.search.search.infrastructure.SearchTrendKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import static com.search.common.exception.enums.ErrorCode.UNABLE_TO_PROCESS;

/**
 * 1.이벤트 리스너가 이벤트 캐치
 * 2. 스코어 업데이트
 * */

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchEventHandler {
    private final SearchTrendKeywordRepository trendKeywordRepository;

    @EventListener
    public void handle(SearchEvent event) {
        try{
            log.info("Receive {}: [event= {}]", ClassUtils.getShortName(event.getClass()),event);
            SearchLog log = event.getSource();
            trendKeywordRepository.updateScoreByKeyword(log.getKeyword(), log.getTimestamp().toLocalDate());
        }
        catch (RuntimeException e) {
            log.error("Fail to execute event. Cause By {}",e.getMessage());
        }
    }

}
