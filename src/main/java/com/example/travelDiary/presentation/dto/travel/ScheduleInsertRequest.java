package com.example.travelDiary.presentation.dto.travel;

import com.example.travelDiary.domain.model.location.Place;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class ScheduleInsertRequest {
    public Place place;
    public Boolean isActuallyVisited;
    public LocalDate travelDate;
    public Integer orderOfTravel;
    public LocalTime travelStartTimeEstimate;
    public LocalTime travelDepartTimeEstimate;
}
