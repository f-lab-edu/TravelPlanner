package com.example.travelDiary.domain.model.location;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
public class Place {
    @Id
    private UUID id;

    public String name;

    public String country;

    public BigDecimal Latitude;

    public BigDecimal Longitude;


}
