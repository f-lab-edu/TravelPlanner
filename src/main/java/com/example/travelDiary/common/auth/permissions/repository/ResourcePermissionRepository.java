package com.example.travelDiary.common.auth.permissions.repository;

import com.example.travelDiary.common.auth.domain.AuthUser;
import com.example.travelDiary.common.auth.permissions.domain.Resource;
import com.example.travelDiary.common.auth.permissions.domain.ResourcePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourcePermissionRepository extends JpaRepository<ResourcePermission, UUID> {
    ResourcePermission findByUserAndResource(AuthUser user, Resource resource);

    Optional<ResourcePermission> findByUserIdAndResourceId(UUID id, UUID resourceId);
}
