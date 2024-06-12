package com.example.travelDiary.presentation.dto.request.travel.schedule;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ScheduleMetadataUpdateRequest {
    public UUID scheduleId;
    public String name;
    public Boolean isActuallyVisited;
//    public LocalDate travelDate;
    public LocalDateTime travelStartTimeEstimate;
    public LocalDateTime travelDepartTimeEstimate;
}
