package com.example.travelDiary.domain.persistence.location;

import com.example.travelDiary.domain.model.location.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {
}
