package com.roamgram.travelDiary.presentation.dto.response.travel;

import com.roamgram.travelDiary.domain.model.location.Place;
import com.roamgram.travelDiary.domain.model.review.Review;
import com.roamgram.travelDiary.domain.model.travel.Route;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ScheduleResponse {

    private UUID id;
    private UUID travelPlanId;
    private String name;
    private String description;
    private Place place;
    private Review review;
    private Boolean isActuallyVisited;
    private LocalDateTime travelStartTimeEstimate;
    private LocalDateTime travelDepartTimeEstimate;
    private Route inwardRoute;
    private Route outwardRoute;

}
