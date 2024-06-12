package com.example.travelDiary.presentation.dto.request.travel.schedule;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class ScheduleMetadataUpdateRequest {
    public UUID scheduleId;
    public Boolean isActuallyVisited;
    public LocalDate travelDate;
    public Integer orderOfTravel;
    public LocalTime travelStartTimeEstimate;
    public LocalTime travelDepartTimeEstimate;
}
