package com.example.travelDiary.common.permissions.service;

import com.example.travelDiary.common.auth.domain.AuthUser;
import com.example.travelDiary.common.auth.service.AuthUserService;
import com.example.travelDiary.common.permissions.domain.Resource;
import com.example.travelDiary.common.permissions.domain.ResourcePermission;
import com.example.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import com.example.travelDiary.common.permissions.repository.ResourcePermissionRepository;
import com.example.travelDiary.common.permissions.repository.ResourceRepository;
import com.example.travelDiary.domain.IdentifiableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class AccessControlService {

    private final ResourceRepository resourceRepository;
    private final ResourcePermissionRepository resourcePermissionRepository;
    private final AuthUserService authUserService;

    @Autowired
    public AccessControlService(ResourceRepository resourceRepository, ResourcePermissionRepository resourcePermissionRepository, AuthUserService authUserService) {
        this.resourceRepository = resourceRepository;
        this.resourcePermissionRepository = resourcePermissionRepository;
        this.authUserService = authUserService;
    }

    public boolean hasPermission(Class<? extends IdentifiableResource> resourceType, UUID resourceId, String permission) {
        Optional<Resource> resourceOpt = resourceRepository.findByResourceUUIDAndType(resourceId, resourceType.getSimpleName());
//                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        if(resourceOpt.isEmpty()) {
            return false;
        }

        Resource resource = resourceOpt.get();
        // If resource is public, allow view permission
        if ("public".equals(resource.getVisibility()) && UserResourcePermissionTypes.VIEW.name().equals(permission)) {
            return true;
        }

        // Check if the current user has permission to assign this permission
        AuthUser currentUser = authUserService.getCurrentAuthenticatedUser();

        Optional<ResourcePermission> resourcePermissionOpt = resourcePermissionRepository.findByUserAndResource(currentUser, resource);
        if (resourcePermissionOpt.isEmpty()) {
            return false;
        }

        try {
            UserResourcePermissionTypes requiredPermission = UserResourcePermissionTypes.valueOf(permission.toUpperCase());
            UserResourcePermissionTypes currentUserPermission = resourcePermissionOpt.get().getPermissions();

            return currentUserPermission.hasHigherOrEqualPermission(requiredPermission);
        } catch (IllegalArgumentException e) {
            // Handle the case where the permission string is not a valid enum value
            return false;
        }

    }

    public void assignPermission(Resource resource, AuthUser user, UserResourcePermissionTypes permission) {
        // Check if the current user has permission to assign this permission
        AuthUser currentUser = authUserService.getCurrentAuthenticatedUser();

        if (!hasPermissionToAssign(currentUser, resource)) {
            throw new AccessDeniedException("You do not have permission to assign this permission");
        }

        ResourcePermission resourcePermission = ResourcePermission.builder()
                .user(user)
                .resource(resource)
                .permissions(permission)
                .build();

        resourcePermissionRepository.save(resourcePermission);
    }

    private boolean hasPermissionToAssign(AuthUser currentUser, Resource resource) {
        Optional<ResourcePermission> resourcePermissionOpt = resourcePermissionRepository.findByUserAndResource(currentUser, resource);
        return resourcePermissionOpt.isPresent() && resourcePermissionOpt.get().getPermissions().compareTo(UserResourcePermissionTypes.EDIT) > 0;
    }

    public void inheritParentPermissions(Resource childResource, Resource parentResource) {
        List<ResourcePermission> parentPermissions = resourcePermissionRepository.findByResource(parentResource);

        for (ResourcePermission parentPermission : parentPermissions) {
            ResourcePermission childPermission = ResourcePermission.builder()
                    .user(parentPermission.getUser())
                    .resource(childResource)
                    .permissions(parentPermission.getPermissions())
                    .build();

            resourcePermissionRepository.save(childPermission);
        }
    }

    public void revokePermission(UUID userId, UUID resourceId) {
        resourcePermissionRepository.deleteByUserIdAndResourceId(userId, resourceId);
    }
}

