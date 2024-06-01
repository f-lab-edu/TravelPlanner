package com.example.travelDiary.application.service.travel.event;

import com.example.travelDiary.application.service.wallet.MonetaryDomainMutationService;
import com.example.travelDiary.domain.model.travel.Event;
import com.example.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.example.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;
import com.example.travelDiary.repository.persistence.travel.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EventMonetaryEventMediator {
    private final EventRepository eventRepository;
    private final MonetaryDomainMutationService monetaryDomainMutationService;

    @Autowired
    public EventMonetaryEventMediator(EventRepository eventRepository, MonetaryDomainMutationService monetaryDomainMutationService) {
        this.eventRepository = eventRepository;
        this.monetaryDomainMutationService = monetaryDomainMutationService;
    }

    @Transactional
    public Event addMonetaryEvent(UUID eventId, MonetaryEvent monetaryEvent) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        List<MonetaryEventEntity> savedMonetaryEvent = monetaryDomainMutationService.save(monetaryEvent);
        List<MonetaryEventEntity> associatedMonetaryEvents = event.getMonetaryEvents();
        associatedMonetaryEvents.addAll(savedMonetaryEvent);
        return eventRepository.save(event);
    }

    @Transactional
    public UUID deleteMonetaryEvent(UUID eventId, UUID monetaryEventId) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        monetaryDomainMutationService.delete(String.valueOf(monetaryEventId));
        return monetaryEventId;
    }

    @Transactional
    public Event updateMonetaryEvent(UUID eventId, UUID monetaryEventId, MonetaryEvent monetaryEvent) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        monetaryDomainMutationService.update(String.valueOf(monetaryEventId), monetaryEvent);
        return eventRepository.save(event);
    }


}
