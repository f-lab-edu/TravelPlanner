package com.roamgram.travelDiary.application.events.review;

import com.roamgram.travelDiary.domain.model.review.Review;
import lombok.Data;

import java.util.UUID;

@Data
public class ReviewCreatedEvent {
    private final UUID scheduleId;
    private final Review review;
    private final boolean isPublic;
}
