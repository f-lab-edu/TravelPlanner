package com.example.travelDiary.common.auth.permissions.service;

import com.example.travelDiary.common.auth.domain.AuthUser;
import com.example.travelDiary.common.auth.permissions.domain.Resource;
import com.example.travelDiary.common.auth.permissions.domain.ResourcePermission;
import com.example.travelDiary.common.auth.permissions.domain.UserResourcePermissionTypes;
import com.example.travelDiary.common.auth.permissions.repository.ResourcePermissionRepository;
import com.example.travelDiary.common.auth.permissions.repository.ResourceRepository;
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
