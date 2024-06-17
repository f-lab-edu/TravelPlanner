package com.example.travelDiary.common.permissions.service;

import com.example.travelDiary.common.auth.domain.AuthUser;
import com.example.travelDiary.common.auth.domain.PrincipalDetails;
import com.example.travelDiary.common.permissions.domain.Resource;
import com.example.travelDiary.common.permissions.domain.ResourcePermission;
import com.example.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import com.example.travelDiary.common.permissions.domain.exception.ResourceNotFoundException;
import com.example.travelDiary.common.permissions.repository.ResourcePermissionRepository;
import com.example.travelDiary.common.permissions.repository.ResourceRepository;
import com.example.travelDiary.domain.IdentifiableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AccessControlService {

    private final ResourceRepository resourceRepository;
    private final ResourcePermissionRepository resourcePermissionRepository;

    @Autowired
    public AccessControlService(ResourceRepository resourceRepository, ResourcePermissionRepository resourcePermissionRepository) {
        this.resourceRepository = resourceRepository;
        this.resourcePermissionRepository = resourcePermissionRepository;
    }

    public boolean hasPermission(Class<? extends IdentifiableResource> resourceType, UUID resourceId, String permission) {
        Resource resource = resourceRepository.findByResourceUUIDAndType(resourceId, resourceType.getSimpleName())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        // If resource is public, allow view permission
        if ("public".equals(resource.getVisibility()) && UserResourcePermissionTypes.VIEW.name().equals(permission)) {
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUser currentUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
        Optional<ResourcePermission> resourcePermissionOpt = resourcePermissionRepository.findByUserAndResource(currentUser, resource);

        return resourcePermissionOpt.isPresent() && resourcePermissionOpt.get().getPermissions().name().equals(permission);
    }

    public void assignOwnerPermission(Resource resource, AuthUser user) {
        ResourcePermission permission = ResourcePermission.builder()
                .user(user)
                .resource(resource)
                .permissions(UserResourcePermissionTypes.OWNER)
                .build();
        resourcePermissionRepository.save(permission);
    }

    public void revokePermission(UUID userId, UUID resourceId) {
        resourcePermissionRepository.deleteByUserIdAndResourceId(userId, resourceId);
    }
}

