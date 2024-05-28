package com.example.travelDiary.repository.persistence.tags;

import com.example.travelDiary.domain.model.tags.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TagsRepository extends JpaRepository<Tags, UUID> {
}
