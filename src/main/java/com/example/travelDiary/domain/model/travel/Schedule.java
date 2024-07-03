package com.example.travelDiary.domain.model.travel;

import com.example.travelDiary.common.permissions.domain.Resource;
import com.example.travelDiary.domain.IdentifiableResource;
import com.example.travelDiary.domain.model.location.Place;
import com.example.travelDiary.domain.model.review.Review;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule implements IdentifiableResource {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    public UUID travelPlanId;

    public String name;

    public String description;

    @ManyToOne(fetch = FetchType.EAGER)
    public Place place;

    @OneToOne
    @Cascade(CascadeType.ALL)
    public Review review;

    public Boolean isActuallyVisited;


    public LocalDateTime travelStartTimeEstimate;

    public LocalDateTime travelDepartTimeEstimate;

    @OneToOne
    @Cascade(CascadeType.ALL)
    public Route inwardRoute;

    @OneToOne
    @Cascade(CascadeType.ALL)
    public Route outwardRoute;

    @OneToMany
    @Cascade(CascadeType.ALL)
    public List<Activity> activities;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name = "resource_id", referencedColumnName = "id")
    private Resource resource;

}
