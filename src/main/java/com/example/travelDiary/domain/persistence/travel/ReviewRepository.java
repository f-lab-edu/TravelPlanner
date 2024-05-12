package com.example.travelDiary.domain.persistence.travel;

import com.example.travelDiary.domain.model.travel.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
