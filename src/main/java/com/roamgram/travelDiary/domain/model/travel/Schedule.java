package com.roamgram.travelDiary.domain.model.travel;

import com.roamgram.travelDiary.common.permissions.domain.Resource;
import com.roamgram.travelDiary.domain.IdentifiableResource;
import com.roamgram.travelDiary.domain.model.location.Place;
import com.roamgram.travelDiary.domain.model.review.Review;
import com.roamgram.travelDiary.domain.model.wallet.entity.MonetaryEventEntity;
import jakarta.persistence.*;
import lombok.*;
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
    @ToString.Exclude
    public List<MonetaryEventEntity> monetaryEvents;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name = "resource_id", referencedColumnName = "id")
    private Resource resource;

}
