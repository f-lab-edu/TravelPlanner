package com.example.travelDiary.common.permissions.service;

import com.example.travelDiary.common.auth.service.AuthUserService;
import com.example.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import com.example.travelDiary.common.permissions.repository.ResourcePermissionRepository;
import com.example.travelDiary.domain.model.user.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ResourcePermissionService {

    private final ResourcePermissionRepository resourcePermissionRepository;
    private final AuthUserService authUserService;

    @Autowired
    public ResourcePermissionService(ResourcePermissionRepository resourcePermissionRepository, AuthUserService authUserService) {
        this.resourcePermissionRepository = resourcePermissionRepository;
        this.authUserService = authUserService;
    }

    public List<UUID> getResourceIdsByUserPermission(UserResourcePermissionTypes permission) {
        UserProfile userProfile = authUserService.getCurrentUser();
        return resourcePermissionRepository.findResourceIdsByUserProfileAndPermission(userProfile, permission.getLevel());
    }

    public List<UUID> getResourceIdsByUserPermissionAndType(UserResourcePermissionTypes permission, String type) {
        UserProfile userProfile = authUserService.getCurrentUser();
        return resourcePermissionRepository.findResourceIdsByUserProfileAndPermissionAndType(userProfile, permission.getLevel(), type);
    }
}
