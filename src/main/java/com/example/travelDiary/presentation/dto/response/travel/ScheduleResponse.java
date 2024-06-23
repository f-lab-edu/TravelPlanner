package com.example.travelDiary.presentation.dto.response.travel;

import com.example.travelDiary.domain.model.location.Place;
import com.example.travelDiary.domain.model.review.Review;
import com.example.travelDiary.domain.model.travel.Activity;
import com.example.travelDiary.domain.model.travel.Route;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
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
    private List<Activity> activities;

}
