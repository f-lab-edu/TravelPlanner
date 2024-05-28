package com.example.travelDiary.repository.persistence.tags;

import com.example.travelDiary.domain.model.tags.TagAssociation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TagAssociationRepository extends JpaRepository<TagAssociation, UUID> {
    List<TagAssociation> findByEntityTypeAndEntityId(String entityType, UUID entityId);

    @Query("SELECT ta.entityId FROM TagAssociation ta WHERE ta.entityType = :entityType AND ta.tag.name IN :tagNames GROUP BY ta.entityId HAVING COUNT(DISTINCT ta.tag.name) = :tagCount")
    List<UUID> findEntityIdsByTags(@Param("entityType") String entityType, @Param("tagNames") List<String> tagNames, @Param("tagCount") long tagCount);

    @Query("SELECT ta.entityId FROM TagAssociation ta WHERE ta.entityId IN :entityIds AND ta.tag.name IN :tagNames GROUP BY ta.entityId HAVING COUNT(DISTINCT ta.tag.name) = :tagCount")
    List<UUID> filterEntityIdsByTags(@Param("entityIds") List<UUID> entityIds, @Param("tagNames") List<String> tagNames, @Param("tagCount") long tagCount);

    void deleteByTagIdAndEntityId(UUID tagId, UUID entityId);

    List<TagAssociation> findByTagIdAndEntityId(UUID tagId, UUID entityId);
}