package com.roamgram.travelDiary.repository.persistence.review;

import com.roamgram.travelDiary.domain.model.review.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Page<Review> findAllByScheduleId(UUID scheduleId, Pageable pageable);

    @Query("SELECT r FROM Review r" +
            " WHERE r.resource.id IN :resourceIds")
    Page<Review> findAllFromAuthorizedIds(@Param("resourceIds")List<UUID> resourceIds, Pageable pageable);

    @Query("SELECT r from Review r" +
            " WHERE r.scheduleId IN :scheduleIds")
    Page<Review> getAllReviewsFromScheduleIds(@Param("scheduleIds")List<UUID> scheduleIds, Pageable pageable);

    @Query("SELECT r FROM Review r" +
            " JOIN Schedule s ON r.scheduleId = s.id" +
            " JOIN s.place p" +
            " WHERE r.resource.id IN :resourceIds" +
            " AND p.googleMapsKeyId = :googleMapsId")
    Page<Review> findAllFromAuthorizedIdsAndPlace(@Param("resourceIds") List<UUID> resourceIds,
                                                  @Param("googleMapsId") String googleMapsId,
                                                  Pageable pageable);
}
