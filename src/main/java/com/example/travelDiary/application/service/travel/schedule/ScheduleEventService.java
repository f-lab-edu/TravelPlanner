package com.example.travelDiary.application.service.travel.schedule;

import com.example.travelDiary.application.service.travel.EventAccessService;
import com.example.travelDiary.domain.model.travel.Event;
import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.example.travelDiary.presentation.dto.request.travel.event.EventMetaDataUpsertRequest;
import com.example.travelDiary.repository.persistence.travel.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ScheduleEventService {

    private final EventAccessService eventAccessService;
    private final ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleEventService(EventAccessService eventAccessService, ScheduleRepository scheduleRepository) {
        this.eventAccessService = eventAccessService;
        this.scheduleRepository = scheduleRepository;
    }

    public Event addEvent(EventMetaDataUpsertRequest request) {
        Schedule schedule = scheduleRepository.findById(request.getScheduleId()).orElseThrow();
        Event event = eventAccessService.createEvent(request);
        schedule.getEvents().add(event);
        return event;
    }

    public UUID deleteEvent(UUID eventId) {
        UUID deletedId = eventAccessService.deleteEvent(eventId);
        return deletedId;
    }

    public Event updateEvent(EventMetaDataUpsertRequest request) {
        Schedule schedule = scheduleRepository.findById(request.getScheduleId()).orElseThrow();
        Event event = eventAccessService.updateEventMetaData(request);
        schedule.getEvents().add(event);
        return event;
    }

    public Page<MonetaryEvent> getAssociatedMonetaryEvent(UUID scheduleId, PageRequest pageRequest) {
        List<Event> events = scheduleRepository.findById(scheduleId).orElseThrow().getEvents();
        List<MonetaryEvent> monetaryEvents = events.stream().flatMap(event -> eventAccessService.getAllMonetaryEvents(event.getId()).stream()).toList();
        return new PageImpl<>(monetaryEvents, pageRequest, events.size());
    }


}
