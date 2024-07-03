package com.example.travelDiary.common.permissions.repository;

import com.example.travelDiary.common.permissions.domain.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, UUID> {
    @Query("SELECT r From Resource r Where r.resourceUUID = :resourceId AND r.type = :simpleName")
    Optional<Resource> findByResourceUUIDAndType(@Param("resourceId")UUID resourceId, @Param("simpleName")String simpleName);

    List<Resource> findAllByResourceUUIDIn(Collection<UUID> resourceUUID);
}
