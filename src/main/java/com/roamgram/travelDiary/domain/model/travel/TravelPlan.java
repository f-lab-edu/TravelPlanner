package com.roamgram.travelDiary.domain.model.travel;

import com.roamgram.travelDiary.common.permissions.domain.Resource;
import com.roamgram.travelDiary.domain.IdentifiableResource;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelPlan implements IdentifiableResource {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    public String name;

    public LocalDate travelStartDate;

    public LocalDate travelEndDate;

    public boolean isPublic;

    @OneToMany
    @Cascade(CascadeType.ALL)
    public List<Schedule> ScheduleList;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name = "resource_id", referencedColumnName = "id")
    private Resource resource;
}
