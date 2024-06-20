package com.example.travelDiary.common.permissions.repository;

import com.example.travelDiary.common.permissions.domain.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, UUID> {
    Optional<Resource> findByResourceUUIDAndType(UUID resourceId, String simpleName);

    List<Resource> findAllByResourceUUIDIn(Collection<UUID> resourceUUID);
}
