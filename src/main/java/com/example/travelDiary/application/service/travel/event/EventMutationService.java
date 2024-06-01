package com.example.travelDiary.application.service.travel.event;

import com.example.travelDiary.domain.model.travel.Event;
import com.example.travelDiary.presentation.dto.request.travel.event.EventMetaDataUpsertRequest;
import com.example.travelDiary.repository.persistence.travel.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EventMutationService {
    private final ConversionService conversionService;
    private final EventRepository eventRepository;

    @Autowired
    public EventMutationService(ConversionService conversionService, EventRepository eventRepository) {
        this.conversionService = conversionService;
        this.eventRepository = eventRepository;
    }

    public Event createEvent(EventMetaDataUpsertRequest request) {
        Event event = conversionService.convert(request, Event.class);
        assert event != null;
        return eventRepository.save(event);
    }

    public Event updateEventMetaData(EventMetaDataUpsertRequest request) {
        Event event = eventRepository.findById(request.getId()).orElseThrow();
        event.setEventEndTime(request.getEventEndTime());
        event.setEventStartTime(request.getEventStartTime());
        return eventRepository.save(event);
    }

    public UUID deleteEvent(UUID eventId) {
        eventRepository.deleteById(eventId);
        return eventId;
    }


}
