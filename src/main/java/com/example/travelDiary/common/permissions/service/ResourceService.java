package com.example.travelDiary.common.permissions.service;

import com.example.travelDiary.common.permissions.domain.Resource;
import com.example.travelDiary.common.permissions.domain.exception.ResourceNotFoundException;
import com.example.travelDiary.common.permissions.repository.ResourceRepository;
import com.example.travelDiary.domain.IdentifiableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ResourceService {
    private final ResourceRepository resourceRepository;

    @Autowired
    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public Resource getResourceById(UUID resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
    }

    public Resource createResource(String visibility, UUID resourceUUID, String type) {
        Resource resource = Resource.builder()
                .visibility(visibility)
                .resourceUUID(resourceUUID)
                .type(type)
                .createTime(Instant.now())
                .build();
        return resourceRepository.save(resource);
    }

    public Resource linkResource(IdentifiableResource identifiableResource, String visibility) {
        return createResource(visibility, identifiableResource.getId(), identifiableResource.getClass().getSimpleName());
    }

    public void deleteResourceById(UUID resourceId) {
        resourceRepository.deleteById(resourceId);
    }

}