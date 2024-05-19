package com.example.travelDiary.presentation.converter;

import com.example.travelDiary.domain.model.location.Place;
import com.example.travelDiary.presentation.dto.travel.PlaceUpdateRequest;
import org.springframework.core.convert.converter.Converter;

public class PlaceUpdateRequestToEntity implements Converter<PlaceUpdateRequest, Place> {

    @Override
    public Place convert(PlaceUpdateRequest source) {
        Place place = new Place();

        if (source.getGoogleMapsKeyId() != null) {
            place.setGoogleMapsKeyId(source.getGoogleMapsKeyId());
        }

        if (source.getName() != null) {
            place.setName(source.getName());
        }

        if (source.getCountry() != null) {
            place.setCountry(source.getCountry());
        }

        if (source.getVisitedCount() != null) {
            place.setVisitedCount(source.getVisitedCount());
        }

        if (source.getLatitude() != null) {
            place.setLatitude(source.getLatitude());
        }

        if (source.getLongitude() != null) {
            place.setLongitude(source.getLongitude());
        }

        return place;
    }
}