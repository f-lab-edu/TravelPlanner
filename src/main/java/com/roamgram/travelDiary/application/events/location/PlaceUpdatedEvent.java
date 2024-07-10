package com.roamgram.travelDiary.application.events.location;

import com.roamgram.travelDiary.domain.model.location.Place;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlaceUpdatedEvent {
    private final Place place;
}
