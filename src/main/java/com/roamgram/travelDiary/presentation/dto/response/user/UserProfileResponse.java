package com.roamgram.travelDiary.presentation.dto.response.user;

import lombok.Data;

import java.util.UUID;

@Data
public class UserProfileResponse {

    private UUID id;

    private String userProfileName;

    private String userDescription;
}
