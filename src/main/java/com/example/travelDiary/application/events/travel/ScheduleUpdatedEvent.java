package com.example.travelDiary.application.events.travel;

import com.example.travelDiary.domain.model.travel.schedule.Schedule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ScheduleUpdatedEvent {
    private final Schedule schedule;
}
