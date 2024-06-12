package com.example.travelDiary.application.events.travel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class SchedulePreDeletedEvent {
    private final UUID travelPlanId;
    private final UUID scheduleId;
}
