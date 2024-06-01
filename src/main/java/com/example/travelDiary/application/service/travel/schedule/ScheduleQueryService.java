package com.example.travelDiary.application.service.travel.schedule;

import com.example.travelDiary.application.service.travel.event.EventAccessService;
import com.example.travelDiary.domain.model.travel.Event;
import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.example.travelDiary.repository.persistence.travel.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ScheduleQueryService {
    private final ScheduleRepository scheduleRepository;
    private final EventAccessService eventAccessService;

    @Autowired
    public ScheduleQueryService(ScheduleRepository scheduleRepository, EventAccessService eventAccessService) {
        this.scheduleRepository = scheduleRepository;
        this.eventAccessService = eventAccessService;
    }

    public Schedule getSchedule(UUID scheduleId) {
        return scheduleRepository.getReferenceById(scheduleId);
    }

    public Page<Schedule> getSchedulesOnGivenDay (LocalDate date, Integer pageNumber, Integer pageSize) {
        PageRequest pageable = PageRequest.of(pageNumber,pageSize);
        return scheduleRepository.findAllByTravelDate(date, pageable);
    }

    public Page<Schedule> getScheduleContainingName(String name, Integer pageNumber, Integer pageSize) {
        PageRequest pageable = PageRequest.of(pageNumber,pageSize);
        return scheduleRepository.findAllByPlaceNameContaining(name, pageable);
    }

    public List<MonetaryEvent> getAssociatedMonetaryEvent(UUID scheduleId) {
        List<Event> events = scheduleRepository.findById(scheduleId).orElseThrow().getEvents();
        return events.stream().flatMap(event -> eventAccessService.getAllMonetaryEvents(event.getId()).stream()).toList();
    }

}
