//package com.common.event;
//
//import com.search.domain.SearchLog;
//import com.search.infrastructure.SearchTrendKeywordRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class SearchEventHandler {
//    private final SearchTrendKeywordRepository trendKeywordRepository;
//
//    @EventListener
//    public void handle(){
//        SearchLog searchLog = message.get;
//        trendKeywordRepository.updateScoreByKeyword(searchLog.getKeyword(),s);
//    }
//
//}
