package com.roamgram.travelDiary.common.permissions.repository;

import com.roamgram.travelDiary.common.permissions.domain.Resource;
import com.roamgram.travelDiary.common.permissions.domain.ResourcePermission;
import com.roamgram.travelDiary.domain.model.user.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResourcePermissionRepository extends JpaRepository<ResourcePermission, UUID> {
    Optional<ResourcePermission> findByUserProfileAndResource(UserProfile userProfile, Resource resource);
    List<ResourcePermission> findByUserProfile(UserProfile userProfile);
    List<ResourcePermission> findByResource(Resource resource);
    void deleteByUserProfileIdAndResourceId(UUID userId, UUID resourceId);
    void deleteAllByResourceIn(List<Resource> resourceId);
    void deleteAllByResourceIdIn(List<UUID> resourceIds);

    @Query("SELECT rp.resource.id FROM ResourcePermission rp " +
            "WHERE rp.userProfile = :user " +
            "AND rp.permissionsLevel >= :permissionLevel")
    List<UUID> findResourceIdsByUserProfileAndPermission(@Param("user") UserProfile user,
                                                         @Param("permissionLevel") int permissionLevel);


    @Query("SELECT rp.resource.id FROM ResourcePermission rp " +
            "JOIN rp.resource r " +
            "WHERE rp.userProfile = :user " +
            "AND rp.permissionsLevel >= :permissionLevel " +
            "AND r.type = :type")
    List<UUID> findResourceIdsByUserProfileAndPermissionAndType(@Param("user") UserProfile user,
                                                                @Param("permissionLevel") int permissionLevel,
                                                                @Param("type") String type);

}

