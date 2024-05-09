package com.example.travelDiary.domain.persistence.travel;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class TravelPlan {

    @Id
    private Long id;

    @OneToMany
    public List<Schedule> ScheduleList;
}
