package com.example.travelDiary.repository.persistence.travel;

import com.example.travelDiary.domain.model.travel.schedule.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {

    public Page<Schedule> findAllByTravelDate(LocalDate date, Pageable pageable);

    Page<Schedule> findAllByPlaceNameContaining(String name, Pageable pageable);

    List<Schedule> findByPlaceId(UUID id);
}
