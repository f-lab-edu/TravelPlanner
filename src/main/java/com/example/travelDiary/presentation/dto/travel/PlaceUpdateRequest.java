package com.example.travelDiary.presentation.dto.travel;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PlaceUpdateRequest {

    public UUID scheduleId;

    public String googleMapsKeyId;

    public String name;

    public String country;

    public Integer visitedCount;

    public BigDecimal Latitude;

    public BigDecimal Longitude;
}
