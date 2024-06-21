package com.example.travelDiary.application.events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public EventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishEvent(Object event) {
        applicationEventPublisher.publishEvent(event);
    }

}