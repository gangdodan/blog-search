package com.search.search.event;

import com.search.search.domain.SearchLog;
import org.springframework.context.ApplicationEvent;

/**
 * ApplicationEvent를 상속받아서 SearchLog 객체를 이벤트로 감싸고 필요한 정보를 제공
 * */
public class SearchEvent extends ApplicationEvent {

    //Application source 필드에 log 객체 할당
    private SearchEvent(SearchLog log) {
        super(log);
    }

    public static SearchEvent of(SearchLog log){
        return new SearchEvent(log);
    }

    public SearchLog getSource() {
        return (SearchLog)this.source;
    }
}
