package com.example.travelDiary.domain.persistence.travel;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.List;

@Entity
@Data
public class TravelPlan {

    @Id
    private Long id;

    @OneToMany
    @Cascade(CascadeType.ALL)
    public List<Schedule> ScheduleList;
}
