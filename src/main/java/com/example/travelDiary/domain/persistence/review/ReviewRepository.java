package com.example.travelDiary.domain.persistence.review;

import com.example.travelDiary.domain.model.review.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Page<Review> findAllByScheduleId(UUID scheduleId, Pageable pageable);
}
