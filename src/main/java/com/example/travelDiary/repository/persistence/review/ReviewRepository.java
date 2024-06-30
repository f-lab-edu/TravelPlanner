package com.example.travelDiary.repository.persistence.review;

import com.example.travelDiary.domain.model.review.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Page<Review> findAllByScheduleId(UUID scheduleId, Pageable pageable);

    @Query("SELECT r FROM Review r" +
            " WHERE r.resource.id IN :resourceIds")
    Page<Review> findAllAuthorized(List<UUID> resourceIds, Pageable pageable);

    @Query("SELECT r from Review r" +
            " WHERE r.scheduleId IN :scheduleIds")
    Page<Review> getAllReviewsFromScheduleIds(List<UUID> scheduleIds, Pageable pageable);
}
