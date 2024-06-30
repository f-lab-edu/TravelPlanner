package com.example.travelDiary.repository.persistence.travel;

import com.example.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import com.example.travelDiary.domain.model.travel.TravelPlan;
import com.example.travelDiary.domain.model.user.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TravelPlanRepository extends JpaRepository<TravelPlan, UUID> {

    Page<TravelPlan> findAllByNameContaining(String name, Pageable pageable);

    @Query("SELECT tp FROM TravelPlan tp JOIN tp.resource r JOIN ResourcePermission rp ON rp.resource = r " +
            "WHERE tp.name LIKE %:name% AND rp.userProfile = :user AND rp.permissions >= :permission")
    Page<TravelPlan> findAllByNameContainingAndUserPermission(@Param("name") String name,
                                                              @Param("user") UserProfile user,
                                                              @Param("permission") UserResourcePermissionTypes permission,
                                                              Pageable pageable);

    @Query("SELECT tp FROM TravelPlan tp " +
            "WHERE tp.resource.id IN :resourceIds " +
            "AND tp.name LIKE %:name%")
    Page<TravelPlan> findAllByNameContainingAndResourceIds(@Param("name") String name,
                                                           @Param("resourceIds") List<UUID> resourceIds,
                                                           Pageable pageable);

    @Query("SELECT tp FROM TravelPlan tp " +
            "WHERE tp.resource.id IN :resourceIds ")
    List<TravelPlan> findAllByResourceIds(List<UUID> resourceIds);
}

