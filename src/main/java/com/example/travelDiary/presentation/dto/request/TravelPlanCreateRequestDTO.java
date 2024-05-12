package com.example.travelDiary.presentation.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TravelPlanCreateRequestDTO {
    public String name;
    public LocalDate startDate;
    public LocalDate endDate;
}
