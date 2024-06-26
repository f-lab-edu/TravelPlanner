package com.example.travelDiary.application.events.review;

import com.example.travelDiary.domain.model.review.Review;
import lombok.Data;

import java.util.UUID;

@Data
public class ReviewCreatedEvent {
    private final UUID scheduleId;
    private final Review review;
}
