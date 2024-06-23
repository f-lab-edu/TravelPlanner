package com.example.travelDiary.presentation.converter.request.travel;

import com.example.travelDiary.domain.model.travel.Route;
import com.example.travelDiary.presentation.dto.request.travel.RouteUpdateRequest;
import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;
import java.util.UUID;

public class RouteUpdateRequestToEntity implements Converter<RouteUpdateRequest, Route> {
    @Override
    public Route convert(RouteUpdateRequest source) {
        Route route = new Route();

        if (source.getId() != null) {
            route.setId(source.getId());
        } else {
            route.setId(UUID.randomUUID());
        }

        if (source.getOutBoundScheduleId() != null) {
            route.setOutBoundScheduleId(source.getOutBoundScheduleId());
        }

        if (source.getInBoundScheduleId() != null) {
            route.setInBoundScheduleId(source.getInBoundScheduleId());
        }

        if (source.getDurationOfTravel() != null) {
            route.setDurationOfTravel(source.getDurationOfTravel());
        }

        if (source.getDistanceOfTravel() != null) {
            route.setDistanceOfTravel(source.getDistanceOfTravel());
        } else {
            route.setDistanceOfTravel(BigDecimal.ZERO);
        }

        if (source.getMethodOfTravel() != null) {
            route.setMethodOfTravel(source.getMethodOfTravel());
        } else {
            route.setMethodOfTravel("");
        }

        if (source.getGoogleEncodedPolyline() != null) {
            route.setGoogleEncodedPolyline(source.getGoogleEncodedPolyline());
        } else {
            route.setGoogleEncodedPolyline("");
        }

        return route;
    }
}
