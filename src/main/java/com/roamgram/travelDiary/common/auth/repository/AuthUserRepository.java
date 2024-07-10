package com.roamgram.travelDiary.common.auth.repository;

import com.roamgram.travelDiary.common.auth.domain.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthUserRepository  extends JpaRepository<AuthUser, UUID> {
    Optional<AuthUser> findByUsername(String username);

    Optional<AuthUser> findByProviderId(String authUserId);

    Optional<Object> findByEmail(String email);
}
