package com.roamgram.travelDiary.presentation.dto.response.travel;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class TravelPlanResponse {
    private UUID id;
    private String name;
    private LocalDate travelStartDate;
    private LocalDate travelEndDate;
    private boolean isPublic;
    private List<ScheduleResponse> scheduleList;
}
