package com.example.travelDiary.application.service.tags;

import com.example.travelDiary.domain.model.tags.TagAssociation;
import com.example.travelDiary.domain.model.tags.Tags;
import com.example.travelDiary.repository.persistence.tags.TagAssociationRepository;
import com.example.travelDiary.repository.persistence.tags.TagsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TagsAccessService {

    private final TagsRepository tagsRepository;
    private final TagAssociationRepository tagAssociationRepository;

    @Autowired
    public TagsAccessService(TagsRepository tagsRepository, TagAssociationRepository tagAssociationRepository) {
        this.tagsRepository = tagsRepository;
        this.tagAssociationRepository = tagAssociationRepository;
    }

    @Transactional
    public Tags createTag(String name, String description) {
        Tags tag = new Tags();
        tag.setName(name);
        tag.setDescription(description);
        return tagsRepository.save(tag);
    }

    @Transactional
    public void addTagToEntity(UUID tagId, String entityType, UUID entityId) {
        Tags tag = tagsRepository.findById(tagId).orElseThrow(() -> new RuntimeException("Tag not found"));
        TagAssociation tagAssociation = new TagAssociation();
        tagAssociation.setTag(tag);
        tagAssociation.setEntityType(entityType);
        tagAssociation.setEntityId(entityId);
        tagAssociation.setTimestamp(LocalDateTime.now());
        tagAssociationRepository.save(tagAssociation);
    }

    public List<UUID> filterEntitiesByTags(List<UUID> entityIds, List<String> tagNames) {
        long tagCount = tagNames.size();
        // Filter the entity IDs that match the tag criteria
        return tagAssociationRepository.filterEntityIdsByTags(entityIds, tagNames, tagCount);
    }

    @Transactional
    public List<Tags> getTagsForEntity(String entityType, UUID entityId) {
        List<TagAssociation> associations = tagAssociationRepository.findByEntityTypeAndEntityId(entityType, entityId);
        return associations.stream().map(TagAssociation::getTag).toList();
    }

    @Transactional
    public void deleteTagFromEntity(UUID tagId, UUID entityId) {
        List<TagAssociation> associations = tagAssociationRepository.findByTagIdAndEntityId(tagId, entityId);
        tagAssociationRepository.deleteAll(associations);
    }
}
