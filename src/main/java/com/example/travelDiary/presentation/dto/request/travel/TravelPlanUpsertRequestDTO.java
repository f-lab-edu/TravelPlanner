package com.example.travelDiary.presentation.dto.request.travel;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class TravelPlanUpsertRequestDTO {
    public UUID uuid;
    public String name;
    public LocalDate startDate;
    public LocalDate endDate;



}
