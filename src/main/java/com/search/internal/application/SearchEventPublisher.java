package com.search.internal.application;

import com.search.search.event.SearchEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    public void publish(SearchEvent source){
        eventPublisher.publishEvent(source);
    }

}
