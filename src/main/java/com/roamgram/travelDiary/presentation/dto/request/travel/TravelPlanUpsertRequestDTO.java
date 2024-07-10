package com.roamgram.travelDiary.presentation.dto.request.travel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelPlanUpsertRequestDTO {
    public UUID uuid;
    public String name;
    public LocalDate startDate;
    public LocalDate endDate;
}
