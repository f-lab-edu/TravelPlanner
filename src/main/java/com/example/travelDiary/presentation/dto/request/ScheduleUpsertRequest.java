package com.example.travelDiary.presentation.dto.request;

import com.example.travelDiary.domain.model.location.Place;
import com.example.travelDiary.domain.model.travel.Review;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ScheduleUpsertRequest {
    public Place place;
    public Review review;
    public Boolean isActuallyVisited;
    public LocalDate travelDate;
    public Integer orderOfTravel;
    public LocalTime travelStartTimeEstimate;
    public LocalTime travelDepartTimeEstimate;
}
