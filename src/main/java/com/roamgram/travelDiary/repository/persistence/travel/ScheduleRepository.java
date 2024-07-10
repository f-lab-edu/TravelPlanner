package com.roamgram.travelDiary.repository.persistence.travel;

import com.roamgram.travelDiary.domain.model.travel.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {

    @Query("SELECT s FROM Schedule s" +
            " WHERE (s.travelStartTimeEstimate BETWEEN :startOfDay AND :endOfDay)" +
            " AND s.resource.id IN :resourceIds")
    Page<Schedule> findAllByTravelDate(@Param("startOfDay") LocalDateTime startOfDay,
                                       @Param("endOfDay") LocalDateTime endOfDay,
                                       @Param("resourceIds") List<UUID> resourceIds,
                                       Pageable pageable);


    @Query("SELECT s FROM Schedule s" +
            " WHERE s.place.name LIKE :name" +
            " AND s.resource.id IN :resourceIds")
    Page<Schedule> findAllByPlaceNameContaining(@Param("name") String name,
                                                @Param("resourceIds") List<UUID> resourceIds,
                                                Pageable pageable);

    @Query("SELECT s FROM Schedule s" +
            " WHERE s.travelPlanId = :travelPlanId" +
            " AND s.resource.id IN :resourceIds")
    List<Schedule> findAllByTravelPlanId(@Param("travelPlanId") UUID travelPlanId,
                                         @Param("resourceIds") List<UUID> resourceIds);

    List<Schedule> findByPlaceId(UUID id);

}
