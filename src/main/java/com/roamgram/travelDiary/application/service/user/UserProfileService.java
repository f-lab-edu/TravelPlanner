package com.roamgram.travelDiary.application.service.user;

import com.roamgram.travelDiary.common.permissions.domain.exception.ResourceNotFoundException;
import com.roamgram.travelDiary.domain.model.user.UserProfile;
import com.roamgram.travelDiary.presentation.dto.request.user.UserProfileUpdateRequest;
import com.roamgram.travelDiary.repository.persistence.user.UserProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public Page<UserProfile> findUsersByName(String name, Pageable pageable) {
        Page<UserProfile> userProfiles = userProfileRepository.findByUserProfileName(name, pageable);
        return userProfiles;
    }

    @Transactional
    public void changeName(UserProfileUpdateRequest request, UUID userProfileId) {
        UserProfile userProfile = userProfileRepository.findById(userProfileId).orElseThrow(() -> new ResourceNotFoundException("user not found"));
        if(request.getName() != null) {
            userProfile.setUserProfileName(request.getName());
        }
        if(request.getDescription() != null) {
            userProfile.setUserDescription(request.getDescription());
        }
        userProfileRepository.save(userProfile);
    }
}
