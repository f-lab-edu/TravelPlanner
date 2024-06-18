package com.example.travelDiary.application.service.travel;

import com.example.travelDiary.domain.model.travel.Route;
import com.example.travelDiary.repository.persistence.travel.RouteRepository;
import com.example.travelDiary.presentation.dto.request.travel.RouteUpdateRequest;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RouteAccessService {

    private final RouteRepository routeRepository;
    private final ConversionService conversionService;

    public RouteAccessService(RouteRepository routeRepository, ConversionService conversionService) {
        this.routeRepository = routeRepository;
        this.conversionService = conversionService;
    }

    @Transactional
    public Route updateRoute(RouteUpdateRequest request) {
        Route newRoute = conversionService.convert(request, Route.class);
        Optional<Route> existingRouteOpt = routeRepository.findById(request.getId());

        if (existingRouteOpt.isPresent()) {
            Route existingRoute = existingRouteOpt.get();

            assert newRoute != null;
            newRoute.setId(existingRoute.getId());

            return routeRepository.save(newRoute);
        } else {
            throw new IllegalArgumentException("Route with ID " + request.id + " does not exist.");
        }
    }

    @Transactional
    public void removeRoute(Route route) {
        routeRepository.delete(route);
    }

    @Transactional
    public Route createEmptyRoute() {
        Route route = new Route();
        return routeRepository.save(route);
    }

    @Transactional
    public void resetRoute(Route route) {
        if(route == null) {
            return;
        }
        route.setDurationOfTravel(null);
        route.setDistanceOfTravel(null);
        route.setMethodOfTravel(null);
        route.setGoogleEncodedPolyline(null);
        routeRepository.save(route);
    }
}
