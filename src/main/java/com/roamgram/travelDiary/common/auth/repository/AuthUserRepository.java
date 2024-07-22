package com.roamgram.travelDiary.common.auth.repository;

import com.roamgram.travelDiary.common.auth.domain.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {
    Optional<AuthUser> findByUsername(String username);

    Optional<AuthUser> findByProviderId(String authUserId);

    Optional<Object> findByEmail(String email);
}
