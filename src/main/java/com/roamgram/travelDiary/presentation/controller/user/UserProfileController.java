package com.roamgram.travelDiary.presentation.controller.user;

import com.roamgram.travelDiary.application.service.user.UserProfileService;
import com.roamgram.travelDiary.domain.model.user.UserProfile;
import com.roamgram.travelDiary.presentation.dto.request.user.UserProfileUpdateRequest;
import com.roamgram.travelDiary.presentation.dto.response.user.UserProfileResponse;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserProfileController {
    private final UserProfileService userProfileService;
    private final ConversionService conversionService;

    public UserProfileController(UserProfileService userProfileService, ConversionService conversionService) {
        this.userProfileService = userProfileService;
        this.conversionService = conversionService;
    }

    @GetMapping("/find-by-name")
    public Page<UserProfileResponse> searchUsersByName(String name, Integer pageNumber, Integer pageSize){
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<UserProfile> userProfilePage = userProfileService.findUsersByName(name, pageable);
        return userProfilePage.map(userProfile -> conversionService.convert(userProfile, UserProfileResponse.class));
    }

    @PatchMapping("/update-name")
    public ResponseEntity<String> updateUserProfileName(@RequestBody UserProfileUpdateRequest name, UUID userProfileId){
        userProfileService.changeName(name, userProfileId);
        return ResponseEntity.ok("ok");
    }
}
