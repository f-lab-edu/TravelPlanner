package com.example.travelDiary.domain.model.travel;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;

@Entity
@Data
public class Review {
    @Id
    private Long id;


    public String userDescription;
    public Double rating;

}
