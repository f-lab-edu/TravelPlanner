package com.example.travelDiary.domain.persistence.location;

import com.example.travelDiary.domain.model.location.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlaceRepository extends JpaRepository<Place, UUID> {
}
