package com.example.travelDiary.presentation.dto.request.travel.event;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class EventMetaDataUpsertRequest {
    private UUID id;

    public UUID scheduleId;

    public LocalDateTime eventStartTime;

    public LocalDateTime eventEndTime;
}
