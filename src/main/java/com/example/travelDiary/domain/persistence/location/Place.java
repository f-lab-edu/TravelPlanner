package com.example.travelDiary.domain.persistence.location;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Place {
    @Id
    private Long id;

    public String googleMapsKeyId;

    public String name;

    public String country;

    public BigDecimal Latitude;

    public BigDecimal Longitude;


}
