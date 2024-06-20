package com.example.travelDiary.repository.persistence.travel;

import com.example.travelDiary.domain.model.travel.TravelPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TravelPlanRepository extends JpaRepository<TravelPlan, UUID> {
    Page<TravelPlan> findAllByNameContaining(String name, Pageable pageable);
}
