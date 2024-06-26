package com.example.travelDiary.application.events.review;

import lombok.Data;

import java.util.UUID;

@Data
public class ReviewPreDeletedEvent {
    private final UUID scheduleId;
    private final UUID reviewId;
}
