package com.example.travelDiary.application.service;

import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.domain.persistence.travel.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduleAccessService {
    private final ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleAccessService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public Schedule saveSchedule (Schedule schedule) {
        scheduleRepository.save(schedule);
        return schedule;
    }

    public List<Schedule> getSchedulesOnGivenDay (LocalDate date) {
        return scheduleRepository.findAllByTravelDate(date);
    }

    public Schedule getSchedule (LocalDate date, Integer orderOfTravel) {
        return scheduleRepository.findByTravelDateAndOrderOfTravel(date, orderOfTravel);
    }
}
