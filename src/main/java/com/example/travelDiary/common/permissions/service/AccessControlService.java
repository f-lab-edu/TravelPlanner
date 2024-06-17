package com.example.travelDiary.common.permissions.service;

import com.example.travelDiary.common.auth.domain.AuthUser;
import com.example.travelDiary.common.permissions.domain.Resource;
import com.example.travelDiary.common.permissions.domain.ResourcePermission;
import com.example.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import com.example.travelDiary.common.permissions.repository.ResourcePermissionRepository;
import com.example.travelDiary.common.permissions.repository.ResourceRepository;
import com.example.travelDiary.domain.IdentifiableResource;
import org.springframework.beans.factory.annotation.Autowired;
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
        // Implement logic to check permissions for the given resource type and ID
        Optional<Resource> resourceOpt = resourceRepository.findByResourceUUIDAndType(resourceId, resourceType.getSimpleName());
        if (resourceOpt.isEmpty()) {
            return false;
        }

        Resource resource = resourceOpt.get();
        if ("public".equals(resource.getVisibility())) {
            return true;
        }

        // Add additional permission checks here based on user roles, ownership, etc.
        return true; // Placeholder for actual permission logic
    }

    public boolean hasPermission(AuthUser user, UUID resourceId, UserResourcePermissionTypes permissionType) {
        Optional<ResourcePermission> permission = resourcePermissionRepository.findByUserIdAndResourceId(user.getId(), resourceId);
        if (permission.isPresent()) {
            return permission.get().getPermissions().compareTo(permissionType) >= 0;
        } else {
            // Check visibility as fallback
            Optional<Resource> resource = resourceRepository.findById(resourceId);
            if (resource.isPresent()) {
                Resource res = resource.get();
                if ("public".equals(res.getVisibility())) {
                    return permissionType == UserResourcePermissionTypes.VIEW || permissionType == UserResourcePermissionTypes.CLONE;
                }
            }
        }
        return false;
    }

    public void addPermission(AuthUser user, UUID resourceId, UserResourcePermissionTypes permissionType) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found"));
        ResourcePermission resourcePermission = new ResourcePermission();
        resourcePermission.setResource(resource);
        resourcePermission.setUser(user);
        resourcePermission.setPermissions(permissionType);
        resourcePermissionRepository.save(resourcePermission);
    }

    public void revokePermission(UUID userId, UUID resourceId) {
        resourcePermissionRepository.deleteByUserIdAndResourceId(userId, resourceId);
    }


    // Additional methods for managing permissions can be added here
}
