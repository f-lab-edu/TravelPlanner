package com.example.travelDiary.application.events.travel;

import com.example.travelDiary.domain.model.travel.Schedule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ScheduleCreatedEvent {
    private final Schedule schedule;
}
