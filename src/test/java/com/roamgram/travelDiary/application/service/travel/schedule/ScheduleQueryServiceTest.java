package com.roamgram.travelDiary.application.service.travel.schedule;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.roamgram.travelDiary.application.service.travel.event.ActivityAccessService;
import com.roamgram.travelDiary.domain.model.travel.Schedule;
import com.roamgram.travelDiary.domain.model.wallet.aggregate.MonetaryEvent;
import com.roamgram.travelDiary.repository.persistence.travel.ScheduleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ScheduleQueryServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private ActivityAccessService activityAccessService;

    @InjectMocks
    private ScheduleQueryService scheduleQueryService;

    @Test
    void testGetSchedule() {
        UUID scheduleId = UUID.randomUUID();
        Schedule schedule = new Schedule();
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        Schedule result = scheduleQueryService.getSchedule(scheduleId);

        assertEquals(schedule, result);
        verify(scheduleRepository).findById(scheduleId);
    }

    @Test
    void testGetAllAuthorisedSchedulesOnGivenDay() {
        LocalDate date = LocalDate.now();
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Schedule> schedules = new PageImpl<>(Collections.emptyList());
        when(scheduleRepository.findAllByTravelDate(any(), any(), any(), any())).thenReturn(schedules);

        Page<Schedule> result = scheduleQueryService.getAllAuthorisedSchedulesOnGivenDay(date, 0, 10, Collections.emptyList());

        assertEquals(schedules, result);
        verify(scheduleRepository).findAllByTravelDate(
                date.atStartOfDay(),
                date.atTime(LocalTime.MAX),
                Collections.emptyList(),
                pageable
        );
    }

    @Test
    void testGetAllAuthorisedScheduleContainingName() {
        String name = "test";
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Schedule> schedules = new PageImpl<>(Collections.emptyList());
        when(scheduleRepository.findAllByPlaceNameContaining(name, Collections.emptyList(), pageable)).thenReturn(schedules);

        Page<Schedule> result = scheduleQueryService.getAllAuthorisedScheduleContainingName(name, 0, 10, Collections.emptyList());

        assertEquals(schedules, result);
        verify(scheduleRepository).findAllByPlaceNameContaining(name, Collections.emptyList(), pageable);
    }

    @Test
    void testGetAllAuthorisedSchedulesInTravelPlan() {
        UUID travelPlanId = UUID.randomUUID();
        List<Schedule> schedules = Collections.emptyList();
        when(scheduleRepository.findAllByTravelPlanId(travelPlanId, Collections.emptyList())).thenReturn(schedules);

        List<Schedule> result = scheduleQueryService.getAllAuthorisedSchedulesInTravelPlan(travelPlanId, Collections.emptyList());

        assertEquals(schedules, result);
        verify(scheduleRepository).findAllByTravelPlanId(travelPlanId, Collections.emptyList());
    }

    @Test
    void testGetAssociatedMonetaryEvent() {
        UUID scheduleId = UUID.randomUUID();
        Schedule schedule = new Schedule();
        schedule.setActivities(Collections.emptyList());
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        List<MonetaryEvent> result = scheduleQueryService.getAssociatedMonetaryEvent(scheduleId);

        assertTrue(result.isEmpty());
        verify(scheduleRepository).findById(scheduleId);
    }
}

