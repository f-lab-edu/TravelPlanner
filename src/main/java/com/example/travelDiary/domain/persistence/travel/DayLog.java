package com.example.travelDiary.domain.persistence.travel;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;

@Entity
@Data
public class DayLog {

    @Id
    private Long id;

    public LocalDate date;

    Set<SingleTravelPage> new HashSet<>()
}
