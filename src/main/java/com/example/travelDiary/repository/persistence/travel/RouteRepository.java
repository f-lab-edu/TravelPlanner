package com.example.travelDiary.repository.persistence.travel;

import com.example.travelDiary.domain.model.travel.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RouteRepository extends JpaRepository<Route, UUID> {
}
