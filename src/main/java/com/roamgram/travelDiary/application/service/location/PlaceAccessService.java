package com.roamgram.travelDiary.application.service.location;

import com.roamgram.travelDiary.application.service.tags.TagsAccessService;
import com.roamgram.travelDiary.domain.model.location.Place;
import com.roamgram.travelDiary.domain.model.tags.Tags;
import com.roamgram.travelDiary.repository.persistence.location.PlaceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PlaceAccessService {
    private final PlaceRepository placeRepository;
    private final TagsAccessService tagsAccessService;

    @Autowired
    public PlaceAccessService(PlaceRepository placeRepository, TagsAccessService tagsAccessService) {
        this.placeRepository = placeRepository;
        this.tagsAccessService = tagsAccessService;
    }

    @Transactional
    public Place getPlaceById(UUID id) {
        return placeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public List<Place> findPlacesContainingTags(List<Tags> tagsList) {
        return placeRepository.findAllById(tagsAccessService.findEntityIdsByTags("PLACES", tagsList));
    }
}
