package com.example.travelDiary.application.events.travel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ScheduleDeletedEvent {
    private final UUID scheduleId;
    private final UUID placeId;
}
