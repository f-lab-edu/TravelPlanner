package com.example.travelDiary.application.events.location;

import com.example.travelDiary.domain.model.location.Place;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlaceUpdatedEvent {
    private final Place place;
}
