package com.roamgram.travelDiary.presentation.dto.request.travel.event;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ActivityMetaDataUpsertRequest {
    private UUID id;

    public UUID scheduleId;

    public String name;

    public String description;

    public LocalDateTime eventStartTime;

    public LocalDateTime eventEndTime;
}
