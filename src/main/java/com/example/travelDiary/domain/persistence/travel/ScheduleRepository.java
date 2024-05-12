package com.example.travelDiary.domain.persistence.travel;

import com.example.travelDiary.domain.model.travel.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {

    public List<Schedule> findAllByTravelDate(LocalDate date);

    public Schedule findByTravelDateAndOrderOfTravel(LocalDate date, Integer orderOfTravel);
}
