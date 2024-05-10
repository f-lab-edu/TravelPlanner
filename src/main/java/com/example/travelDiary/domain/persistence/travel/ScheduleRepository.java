package com.example.travelDiary.domain.persistence.travel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    public List<Schedule> findAllByTravelDate(LocalDate date);

    public Schedule findByTravelDateAndOrderOfTravel(LocalDate date, Integer orderOfTravel);
}
