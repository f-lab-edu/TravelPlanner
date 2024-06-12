package com.example.travelDiary.application.service.location;

import com.example.travelDiary.application.events.EventPublisher;
import com.example.travelDiary.application.events.location.PlaceDeletedEvent;
import com.example.travelDiary.application.events.location.PlaceUpdatedEvent;
import com.example.travelDiary.domain.model.location.Place;
import com.example.travelDiary.repository.persistence.location.PlaceRepository;
import com.example.travelDiary.presentation.dto.request.travel.location.PlaceUpdateRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class PlaceMutationService {
    private final PlaceRepository placeRepository;
    private final ConversionService conversionService;
    private final EventPublisher eventPublisher;

    public PlaceMutationService(PlaceRepository placeRepository, ConversionService conversionService, EventPublisher eventPublisher) {
        this.placeRepository = placeRepository;
        this.conversionService = conversionService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Place createNewPlaceIfNotExists(PlaceUpdateRequest request) {
        Place place = conversionService.convert(request, Place.class);
        assert place != null;
        Place newPlace = placeRepository
                .findByGoogleMapsKeyId(place.getGoogleMapsKeyId())
                .orElse(placeRepository.save(place));

        return newPlace;
    }

    @Transactional
    public Place updatePlace(PlaceUpdateRequest updateRequest) {
        Place updatedPlace = conversionService.convert(updateRequest, Place.class);
        assert updatedPlace != null;
        Place existingPlace = placeRepository
                .findByGoogleMapsKeyId(updatedPlace.getGoogleMapsKeyId())
                .orElseThrow(() -> new EntityNotFoundException("Place not found"));
        existingPlace.setName(updatedPlace.getName());
        existingPlace.setCountry(updatedPlace.getCountry());
        existingPlace.setLatitude(updatedPlace.getLatitude());
        existingPlace.setLongitude(updatedPlace.getLongitude());
        placeRepository.save(existingPlace);
        eventPublisher.publishEvent(new PlaceUpdatedEvent(existingPlace));
        return existingPlace;
    }

    @Transactional
    public Place createPlace(Place placeFromCreatedSchedule) {
        Place place = placeFromCreatedSchedule;
        Optional<Place> existingPlaceOpt = placeRepository.findByGoogleMapsKeyId(place.getGoogleMapsKeyId());

        if (existingPlaceOpt.isPresent()) {
            place = existingPlaceOpt.get();
        } else {
            placeRepository.save(place);
        }

        return place;
    }

    @Transactional
    public void deletePlace(Place place) {
        placeRepository.deleteById(place.getId());
    }
    @Transactional
    public void deletePlace(UUID placeId) {
        placeRepository.deleteById(placeId);
        eventPublisher.publishEvent(new PlaceDeletedEvent(placeId));
    }

}
