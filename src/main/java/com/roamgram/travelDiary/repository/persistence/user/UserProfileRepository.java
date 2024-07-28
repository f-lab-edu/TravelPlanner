package com.roamgram.travelDiary.repository.persistence.user;

import com.roamgram.travelDiary.domain.model.user.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByAuthUserId(UUID authUserId);

    Page<UserProfile> findByUserProfileName(String name, Pageable pageable);

    Page<UserProfile> findByUserProfileNameContaining(String name, Pageable pageable);
}
