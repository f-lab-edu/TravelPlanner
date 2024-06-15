package com.example.travelDiary.common.auth.permissions.repository;

import com.example.travelDiary.common.auth.permissions.domain.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, UUID> {
}
