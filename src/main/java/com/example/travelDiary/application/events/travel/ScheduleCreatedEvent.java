package com.example.travelDiary.application.events.travel;

import com.example.travelDiary.domain.model.location.Place;
import com.example.travelDiary.domain.model.travel.Schedule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ScheduleCreatedEvent {
    private final Schedule schedule;
    private final UUID travelPlanId;
    private final Place place;
}
