package com.example.travelDiary.presentation.dto.request.travel.schedule;

import com.example.travelDiary.domain.model.location.Place;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class ScheduleInsertRequest {
    public Place place;
    public String name;
    public String description;
    public Boolean isActuallyVisited;
//    public LocalDate travelDate;
    public LocalDateTime travelStartTimeEstimate;
    public LocalDateTime travelDepartTimeEstimate;

    public UUID previousScheduleId;
    public UUID nextScheduleId;
}
