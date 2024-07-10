package com.roamgram.travelDiary.common.permissions.service;

import com.roamgram.travelDiary.common.auth.service.AuthUserService;
import com.roamgram.travelDiary.common.permissions.domain.Resource;
import com.roamgram.travelDiary.common.permissions.domain.ResourcePermission;
import com.roamgram.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import com.roamgram.travelDiary.common.permissions.repository.ResourcePermissionRepository;
import com.roamgram.travelDiary.common.permissions.repository.ResourceRepository;
import com.roamgram.travelDiary.domain.model.user.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ResourcePermissionService {

    private final ResourcePermissionRepository resourcePermissionRepository;
    private final AuthUserService authUserService;
    private final ResourceRepository resourceRepository;

    @Autowired
    public ResourcePermissionService(ResourcePermissionRepository resourcePermissionRepository, AuthUserService authUserService, ResourceRepository resourceRepository) {
        this.resourcePermissionRepository = resourcePermissionRepository;
        this.authUserService = authUserService;
        this.resourceRepository = resourceRepository;
    }

    @Transactional
    public void assignOwnerPermission(Resource resource, UserProfile owner) {
        ResourcePermission permission = ResourcePermission.builder()
                .userProfile(owner)
                .resource(resource)
                .permissions(UserResourcePermissionTypes.OWNER)
                .build();
        String string = permission.toString();
        resourcePermissionRepository.save(permission);
        resource.getPermissions().add(permission);
        resourceRepository.save(resource);
        ResourcePermission tempPerm = resourcePermissionRepository.findById(permission.getId()).orElseThrow();
    }

    public List<UUID> getResourceIdsByUserPermission(UserResourcePermissionTypes permission) {
        UserProfile userProfile = authUserService.getCurrentUser();
        return resourcePermissionRepository.findResourceIdsByUserProfileAndPermission(userProfile, permission.getLevel());
    }

    public List<UUID> getResourceIdsByUserPermissionAndType(UserResourcePermissionTypes permission, String type) {
        UserProfile userProfile = authUserService.getCurrentUser();
        log.info("at ResourcePermissionService#getResourceIdsByUserPermissionAndType, UserProfile {}", userProfile);
        return resourcePermissionRepository.findResourceIdsByUserProfileAndPermissionAndType(userProfile, permission.getLevel(), type);
    }
}
