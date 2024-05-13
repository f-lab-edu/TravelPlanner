package com.example.travelDiary.domain.persistence.review;

import com.example.travelDiary.domain.model.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
}
