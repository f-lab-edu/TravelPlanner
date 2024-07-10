package com.roamgram.travelDiary.presentation.dto.request.travel.schedule;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ScheduleMetadataUpdateRequest {
    public UUID scheduleId;
    public String name;
    public String description;
    public Boolean isActuallyVisited;
//    public LocalDate travelDate;
    public LocalDateTime travelStartTimeEstimate;
    public LocalDateTime travelDepartTimeEstimate;
}
