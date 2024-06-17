package com.example.travelDiary.domain.model.location;

import com.example.travelDiary.domain.IdentifiableResource;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
public class Place implements IdentifiableResource {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    public String googleMapsKeyId;

    public String name;

    public String country;

    public Integer visitedCount;
  
    public BigDecimal Latitude;

    public BigDecimal Longitude;


}
