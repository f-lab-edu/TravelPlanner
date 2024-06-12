package com.example.travelDiary.common.auth.repository;

import com.example.travelDiary.common.auth.domain.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUserRepository  extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> findByUsername(String username);

    Optional<AuthUser> findByProviderId(String authUserId);
}
