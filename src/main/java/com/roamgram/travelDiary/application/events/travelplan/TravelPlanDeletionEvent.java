package com.roamgram.travelDiary.application.events.travelplan;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class TravelPlanDeletionEvent {
    private final List<UUID> resourceIds;
}