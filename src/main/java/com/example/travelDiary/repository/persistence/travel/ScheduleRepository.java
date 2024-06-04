package com.example.travelDiary.repository.persistence.travel;

import com.example.travelDiary.domain.model.travel.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {

    @Query("SELECT s FROM Schedule s WHERE YEAR(s.travelStartTimeEstimate) = Year(:queryDate) AND MONTH(s.travelStartTimeEstimate) = MONTH(:queryDate) AND DAY(s.travelStartTimeEstimate) = DAY(:queryDate)")
    Page<Schedule> findAllByTravelDate(LocalDate queryDate, Pageable pageable);

    Page<Schedule> findAllByPlaceNameContaining(String name, Pageable pageable);

    List<Schedule> findByPlaceId(UUID id);
}
