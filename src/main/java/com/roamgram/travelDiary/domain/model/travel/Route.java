package com.roamgram.travelDiary.domain.model.travel;

import com.roamgram.travelDiary.domain.IdentifiableResource;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Route implements IdentifiableResource {
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
