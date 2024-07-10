package com.roamgram.travelDiary.presentation.dto.request.travel;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class RouteUpdateRequest {

    public UUID id;

    public UUID outBoundScheduleId;

    public UUID inBoundScheduleId;

    public LocalTime durationOfTravel;

    public BigDecimal distanceOfTravel;

    public String methodOfTravel;

    public String GoogleEncodedPolyline;

}
