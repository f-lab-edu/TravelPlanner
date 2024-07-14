package com.roamgram.travelDiary.common.permissions.service;

import com.roamgram.travelDiary.common.auth.service.AuthUserService;
import com.roamgram.travelDiary.common.permissions.domain.Resource;
import com.roamgram.travelDiary.common.permissions.domain.exception.ResourceNotFoundException;
import com.roamgram.travelDiary.common.permissions.repository.ResourcePermissionRepository;
import com.roamgram.travelDiary.common.permissions.repository.ResourceRepository;
import com.roamgram.travelDiary.domain.IdentifiableResource;
import com.roamgram.travelDiary.domain.model.user.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourcePermissionRepository resourcePermissionRepository;
    private final AuthUserService authUserService;
    private final ResourcePermissionService resourcePermissionService;
    private final String PUBLIC = "public";


    @Autowired
    public ResourceService(ResourceRepository resourceRepository,
                           ResourcePermissionRepository resourcePermissionRepository, AuthUserService authUserService,
                           ResourcePermissionService resourcePermissionService) {
        this.resourceRepository = resourceRepository;
        this.resourcePermissionRepository = resourcePermissionRepository;
        this.authUserService = authUserService;
        this.resourcePermissionService = resourcePermissionService;
    }

    public Resource getResourceById(UUID resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
    }

    @Transactional
    public Resource createResource(IdentifiableResource identifiableResource, String visibility) {
        UserProfile owner = authUserService.getCurrentUser();
        String type = identifiableResource.getClass().getSimpleName();
        UUID resourceUUID = identifiableResource.getId();
        Resource resource = Resource.builder()
                .visibility(visibility)
                .resourceUUID(resourceUUID)
                .type(type)
                .createTime(Instant.now())
                .permissions(new ArrayList<>())
                .build();
        resource = resourceRepository.save(resource);

        // Assign OWNER permission to the creator
        resourcePermissionService.assignOwnerPermission(resource, owner);
        return resource;
    }

    @Transactional
    public void deleteAllById(List<UUID> resourceIds) {
        List<UUID> resourceId = resourceRepository.findAllByResourceUUIDIn(resourceIds).stream().map(Resource::getId).toList();
        resourcePermissionRepository.deleteAllByResourceIdIn(resourceId);
        resourceRepository.deleteAllById(resourceId);
    }

    @Transactional
    public void delinkPermissions(List<UUID> resourceIds) {
        List<Resource> resourceId = resourceRepository.findAllByResourceUUIDIn(resourceIds);
        // it should be noted that the deletion of the resourcePermissions technically occur via the deletion of the Resources,
        // but the find then delete method is explicitly used to ensure safeguard against the complex transactional environments,
        // and that the implementation as of current may be modified in the future for greater gains in efficiency by reducing database calls.
        resourcePermissionRepository.deleteAllByResourceIn(resourceId);
        resourcePermissionRepository.flush();
    }

    public List<UUID> getPublicResourceIds(String resourceType) {
        return resourceRepository.findPublicResourceIdsByType(PUBLIC, resourceType);
    }
}
