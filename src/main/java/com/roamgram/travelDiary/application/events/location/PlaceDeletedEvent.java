package com.roamgram.travelDiary.application.events.location;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class PlaceDeletedEvent {
    private final UUID placeId;
}

