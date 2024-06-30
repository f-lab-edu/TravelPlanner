package com.example.travelDiary.repository.persistence.review;

import com.example.travelDiary.domain.model.review.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Page<Review> findAllByScheduleId(UUID scheduleId, Pageable pageable);

    @Query("SELECT r FROM Review r" +
            " WHERE r.resource.id IN :resourceIds")
    Page<Review> findAllAuthorized(List<UUID> resourceIds, Pageable pageable);
}
