package com.roamgram.travelDiary.presentation.dto.request.travel;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TravelPlanCreateRequestDTO {
    public String name;
    public LocalDate startDate;
    public LocalDate endDate;
}
