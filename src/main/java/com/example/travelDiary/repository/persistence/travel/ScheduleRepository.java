package com.example.travelDiary.repository.persistence.travel;

import com.example.travelDiary.domain.model.travel.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {

    @Query("SELECT s FROM Schedule s WHERE " +
            "s.travelStartTimeEstimate BETWEEN :startOfDay AND :endOfDay")
    Page<Schedule> findAllByTravelDate(@Param("startOfDay") LocalDateTime startOfDay,
                                       @Param("endOfDay") LocalDateTime endOfDay,
                                       Pageable pageable);



    Page<Schedule> findAllByPlaceNameContaining(String name, Pageable pageable);

    List<Schedule> findByPlaceId(UUID id);

    List<Schedule> findAllByTravelPlanId(UUID travelPlanId);
}
