package com.example.travelDiary.common.permissions.service;

import com.example.travelDiary.common.auth.service.AuthUserService;
import com.example.travelDiary.common.permissions.domain.Resource;
import com.example.travelDiary.common.permissions.domain.ResourcePermission;
import com.example.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import com.example.travelDiary.common.permissions.domain.exception.ResourceNotFoundException;
import com.example.travelDiary.common.permissions.repository.ResourcePermissionRepository;
import com.example.travelDiary.common.permissions.repository.ResourceRepository;
import com.example.travelDiary.domain.IdentifiableResource;
import com.example.travelDiary.domain.model.user.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourcePermissionRepository resourcePermissionRepository;
    private final AuthUserService authUserService;


    @Autowired
    public ResourceService(ResourceRepository resourceRepository,
                           ResourcePermissionRepository resourcePermissionRepository, AuthUserService authUserService
    ) {
        this.resourceRepository = resourceRepository;
        this.resourcePermissionRepository = resourcePermissionRepository;
        this.authUserService = authUserService;
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
                .build();
        resource = resourceRepository.save(resource);

        // Assign OWNER permission to the creator
        assignOwnerPermission(resource, owner);

        return resource;
    }

    @Transactional
    public void deleteAllById(List<UUID> resourceIds) {
        List<UUID> resourceId = resourceRepository.findAllByResourceUUIDIn(resourceIds).stream().map(Resource::getId).toList();
        resourcePermissionRepository.deleteAllByResourceIdIn(resourceId);
        resourceRepository.deleteAllById(resourceId);
    }

    private void assignOwnerPermission(Resource resource, UserProfile owner) {
        ResourcePermission permission = ResourcePermission.builder()
                .userProfile(owner)
                .resource(resource)
                .permissions(UserResourcePermissionTypes.OWNER)
                .build();
        resourcePermissionRepository.save(permission);
    }

    @Transactional
    public void delinkPermissions(List<UUID> resourceIds) {
        List<Resource> resources = resourceRepository.findAll();
        List<Resource> resourceId = resourceRepository.findAllByResourceUUIDIn(resourceIds);
        resourcePermissionRepository.deleteAllByResourceIn(resourceId);
        resourcePermissionRepository.flush();
    }
}
