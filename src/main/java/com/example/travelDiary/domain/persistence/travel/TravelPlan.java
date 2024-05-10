package com.example.travelDiary.domain.persistence.travel;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class TravelPlan {

    @Id
    private Long id;

    public String name;

    public LocalDate travelStartDate;

    public LocalDate travelEndDate;

    @OneToMany
    @Cascade(CascadeType.ALL)
    public List<Schedule> ScheduleList;
}
