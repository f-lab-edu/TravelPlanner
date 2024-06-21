package com.example.travelDiary.common.permissions.repository;

import com.example.travelDiary.common.permissions.domain.Resource;
import com.example.travelDiary.common.permissions.domain.ResourcePermission;
import com.example.travelDiary.domain.model.user.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResourcePermissionRepository extends JpaRepository<ResourcePermission, UUID> {
    Optional<ResourcePermission> findByUserAndResource(UserProfile userProfile, Resource resource);
    List<ResourcePermission> findByUser(UserProfile userProfile);
    List<ResourcePermission> findByResource(Resource resource);
    void deleteByUserIdAndResourceId(UUID userId, UUID resourceId);
    void deleteByResourceId(UUID resourceId);
    void deleteAllByResourceIdIn(List<UUID> resourceIds);

}

