package com.example.travelDiary.domain.persistence.travel;

import com.example.travelDiary.domain.model.travel.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RouteRepository extends JpaRepository<Route, UUID> {
}
