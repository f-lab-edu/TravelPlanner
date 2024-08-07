package com.roamgram.travelDiary.common.permissions.service;

import com.roamgram.travelDiary.common.auth.domain.AuthUser;
import com.roamgram.travelDiary.common.auth.service.AuthUserService;
import com.roamgram.travelDiary.common.permissions.domain.Resource;
import com.roamgram.travelDiary.common.permissions.domain.ResourcePermission;
import com.roamgram.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import com.roamgram.travelDiary.common.permissions.repository.ResourcePermissionRepository;
import com.roamgram.travelDiary.common.permissions.repository.ResourceRepository;
import com.roamgram.travelDiary.domain.IdentifiableResource;
import com.roamgram.travelDiary.domain.model.user.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@Slf4j
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
        try {
            UserResourcePermissionTypes requiredPermission = UserResourcePermissionTypes.valueOf(permission.toUpperCase());

            List<Resource> resourceLS = resourceRepository.findAll();
            log.info("resourceOpt , {}", resourceLS);
            Optional<Resource> resourceOpt = resourceRepository.findByResourceUUIDAndType(resourceId, resourceType.getSimpleName());
//                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
            if (resourceOpt.isEmpty()) {
                return false;
            }

            Resource resource = resourceOpt.get();
            // If resource is public, allow access
            if ("public".equals(resource.getVisibility())) {
                return true;
            }

            // Check if the current user has permission to assign this permission
            UserProfile currentUserProfile = authUserService.getCurrentUser();

            Optional<ResourcePermission> resourcePermissionOpt = resourcePermissionRepository.findByUserProfileAndResource(currentUserProfile, resource);
            if (resourcePermissionOpt.isEmpty()) {
                return false;
            }


            UserResourcePermissionTypes currentUserPermission = resourcePermissionOpt.get().getPermissions();

            return currentUserPermission.hasHigherOrEqualPermission(requiredPermission);
        } catch (IllegalArgumentException e) {
            // Handle the case where the permission string is not a valid enum value
            return false;
        }

    }

    public void assignPermission(Resource resource, AuthUser user, UserResourcePermissionTypes permission) {
        // Check if the current user has permission to assign this permission
        UserProfile currentUserProfile = authUserService.getCurrentUser();

        if (!hasPermissionToAssign(currentUserProfile, resource)) {
            throw new AccessDeniedException("You do not have permission to assign this permission");
        }

        UserProfile assignedUserProfile = authUserService.toUser(user);
        ResourcePermission resourcePermission = ResourcePermission.builder()
                .userProfile(assignedUserProfile)
                .resource(resource)
                .permissions(permission)
                .build();

        resourcePermissionRepository.save(resourcePermission);
    }

    private boolean hasPermissionToAssign(UserProfile currentUserProfile, Resource resource) {
        Optional<ResourcePermission> resourcePermissionOpt = resourcePermissionRepository.findByUserProfileAndResource(currentUserProfile, resource);
        return resourcePermissionOpt.isPresent() && resourcePermissionOpt.get().getPermissions().compareTo(UserResourcePermissionTypes.EDITOR) > 0;
    }

    public void inheritParentPermissions(Resource childResource, Resource parentResource) {
        List<ResourcePermission> parentPermissions = resourcePermissionRepository.findByResource(parentResource);

        for (ResourcePermission parentPermission : parentPermissions) {
            ResourcePermission childPermission = ResourcePermission.builder()
                    .userProfile(parentPermission.getUserProfile())
                    .resource(childResource)
                    .permissions(parentPermission.getPermissions())
                    .build();

            resourcePermissionRepository.save(childPermission);
        }
    }

    public void revokePermission(UUID userId, UUID resourceId) {
        resourcePermissionRepository.deleteByUserProfileIdAndResourceId(userId, resourceId);
    }
}

