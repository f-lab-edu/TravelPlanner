package com.example.travelDiary.common.auth.permissions.service;

import com.example.travelDiary.common.auth.permissions.aop.CheckAccess;
import com.example.travelDiary.common.auth.permissions.domain.Resource;
import com.example.travelDiary.common.auth.permissions.domain.UserResourcePermissionTypes;
import com.example.travelDiary.common.auth.permissions.domain.exception.ResourceNotFoundException;
import com.example.travelDiary.common.auth.permissions.dto.ResourcePermissionUpdateRequest;
import com.example.travelDiary.common.auth.permissions.repository.ResourcePermissionRepository;
import com.example.travelDiary.common.auth.permissions.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ResourceService {

    private final AccessControlService accessControlService;
    private final ResourceRepository resourceRepository;
    private final ResourcePermissionRepository resourcePermissionRepository;

    @Autowired
    public ResourceService(AccessControlService accessControlService, ResourceRepository resourceRepository, ResourcePermissionRepository resourcePermissionRepository) {
        this.accessControlService = accessControlService;
        this.resourceRepository = resourceRepository;
        this.resourcePermissionRepository = resourcePermissionRepository;
    }

    @PreAuthorize("hasPermission(#resourceId, 'VIEW')")
    @CheckAccess(resourceType = "ResourceType", resourceId = "#resourceId", permissionType = UserResourcePermissionTypes.VIEW)
    public Resource getResource(UUID resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
    }

    @PreAuthorize("hasPermission(#resourceId, 'EDIT')")
    @CheckAccess(resourceType = "ResourceType", resourceId = "#resourceId", permissionType = UserResourcePermissionTypes.EDIT)
    public void updateResource(UUID resourceId, ResourcePermissionUpdateRequest updateRequest) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        // Perform update logic here
        resourceRepository.save(resource);
    }

    @PostAuthorize("hasPermission(returnObject.id, 'VIEW')")
    @CheckAccess(resourceType = "ResourceType", resourceId = "#resourceId", permissionType = UserResourcePermissionTypes.VIEW)
    public Resource cloneResource(UUID resourceId) {
        Resource originalResource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        // Perform cloning logic here
        Resource clonedResource = new Resource();
        clonedResource.setVisibility(originalResource.getVisibility());
        clonedResource.setResourceUUID(originalResource.getResourceUUID());
        clonedResource.setType(originalResource.getType());
        clonedResource.setCreateTime(Instant.now());
        resourceRepository.save(clonedResource);
        return clonedResource;
    }
}


