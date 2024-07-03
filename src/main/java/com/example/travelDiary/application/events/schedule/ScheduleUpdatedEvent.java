package com.example.travelDiary.application.events.schedule;

import com.example.travelDiary.domain.model.travel.Schedule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ScheduleUpdatedEvent {
    private final Schedule schedule;
    private final UUID travelPlanId;
}
