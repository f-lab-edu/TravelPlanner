package com.example.travelDiary.application.service.tags;

import com.example.travelDiary.domain.model.tags.Tags;
import com.example.travelDiary.repository.persistence.tags.TagsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagsAccessService {
    private final TagsRepository tagsRepository;

    @Autowired
    public TagsAccessService(TagsRepository tagsRepository) {
        this.tagsRepository = tagsRepository;
    }

//    public Tags createTag()








}
