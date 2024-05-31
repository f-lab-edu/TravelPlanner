package com.example.travelDiary.application.service.travel.schedule;

import com.example.travelDiary.domain.model.travel.schedule.Schedule;
import com.example.travelDiary.repository.persistence.travel.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class ScheduleQueryService {
    private final ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleQueryService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
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

}
