package com.example.travelDiary.domain.persistence.travel;

import com.example.travelDiary.domain.model.location.Place;
import com.example.travelDiary.domain.model.travel.Schedule;
import com.example.travelDiary.domain.persistence.location.PlaceRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ScheduleDomainSupplementalRepository {
    private final PlaceRepository placeRepository;
    private final ScheduleRepository scheduleRepository;

    public ScheduleDomainSupplementalRepository(PlaceRepository placeRepository, ScheduleRepository scheduleRepository) {
        this.placeRepository = placeRepository;
        this.scheduleRepository = scheduleRepository;
    }


}
