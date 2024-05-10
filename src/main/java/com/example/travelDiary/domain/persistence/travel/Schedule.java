package com.example.travelDiary.domain.persistence.travel;

import com.example.travelDiary.domain.persistence.location.Place;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Data
public class Schedule {
    @Id
    private Long id;

    @OneToOne
    public Place place;

    public Boolean isActuallyVisited;

    public LocalDate travelDate;

    public LocalTime travelStartTimeEstimate;

    public LocalTime travelDepartTimeEstimate;

//    public Set<UserTags> travelCategory;


}
