package com.example.travelDiary.domain.model.travel;

import com.example.travelDiary.domain.model.location.Place;
import com.example.travelDiary.domain.model.review.Review;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;


@Entity
@Data
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID TravelPlanId;

    @ManyToOne(fetch = FetchType.EAGER)
    public Place place;

    @OneToOne
    @Cascade(CascadeType.ALL)
    public Review review;

    public Boolean isActuallyVisited;

    public LocalDate travelDate;

    public Integer orderOfTravel;

    public LocalTime travelStartTimeEstimate;

    public LocalTime travelDepartTimeEstimate;

    @OneToOne
    @Cascade(CascadeType.ALL)
    public Route inwardRoute;

    @OneToOne
    @Cascade(CascadeType.ALL)
    public Route outwardRoute;

    @OneToMany
    @Cascade(CascadeType.ALL)
    public List<Activity> activities;
}
