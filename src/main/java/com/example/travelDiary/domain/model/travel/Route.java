package com.example.travelDiary.domain.model.travel;

import com.example.travelDiary.domain.model.location.Place;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Entity
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    public UUID outBoundScheduleId;

    public UUID inBoundScheduleId;

    public LocalTime durationOfTravel;

    public BigDecimal distanceOfTravel;

    public String methodOfTravel;

    public String GoogleEncodedPolyline;

}
