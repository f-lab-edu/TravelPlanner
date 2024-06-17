package com.example.travelDiary.common.permissions.repository;

import com.example.travelDiary.common.auth.domain.AuthUser;
import com.example.travelDiary.common.permissions.domain.Resource;
import com.example.travelDiary.common.permissions.domain.ResourcePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResourcePermissionRepository extends JpaRepository<ResourcePermission, UUID> {
    Optional<ResourcePermission> findByUserAndResource(AuthUser user, Resource resource);
    List<ResourcePermission> findByUser(AuthUser user);
    List<ResourcePermission> findByResource(Resource resource);
    void deleteByUserIdAndResourceId(UUID userId, UUID resourceId);
}

