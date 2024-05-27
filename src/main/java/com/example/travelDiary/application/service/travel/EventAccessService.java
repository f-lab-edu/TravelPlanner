package com.example.travelDiary.application.service.travel;

import com.example.travelDiary.domain.model.travel.Event;
import com.example.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.example.travelDiary.presentation.dto.request.travel.event.EventMetaDataUpsertRequest;
import com.example.travelDiary.repository.persistence.travel.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import static com.example.travelDiary.domain.model.wallet.mapper.MonetaryEventMapper.toAggregates;

import java.util.List;
import java.util.UUID;

@Service
public class EventAccessService {
    private final EventRepository eventRepository;
    private final ConversionService conversionService;

    @Autowired
    public EventAccessService(EventRepository eventRepository, ConversionService conversionService) {
        this.eventRepository = eventRepository;
        this.conversionService = conversionService;
    }

    public Event createEvent(EventMetaDataUpsertRequest request) {
        Event event = conversionService.convert(request, Event.class);
        assert event != null;
        return eventRepository.save(event);
    }

    public Event getEventById(UUID id) {
        return eventRepository.findById(id).orElseThrow();
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

    public List<MonetaryEvent> getAllMonetaryEvents(UUID eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        return toAggregates(event.getMonetaryEvents());
    }
}
