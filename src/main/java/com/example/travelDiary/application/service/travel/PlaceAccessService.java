package com.example.travelDiary.application.service.travel;

import com.example.travelDiary.domain.model.location.Place;
import com.example.travelDiary.repository.persistence.location.PlaceRepository;
import com.example.travelDiary.presentation.dto.request.travel.PlaceUpdateRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PlaceAccessService {
    private final PlaceRepository placeRepository;
    private final ConversionService conversionService;

    public PlaceAccessService(PlaceRepository placeRepository, ConversionService conversionService) {
        this.placeRepository = placeRepository;
        this.conversionService = conversionService;
    }

    @Transactional
    public Place reassignPlace(PlaceUpdateRequest request) {
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
        return placeRepository.save(existingPlace);
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

    public void deletePlace(Place place) {
        placeRepository.deleteById(place.getId());
    }

}
