package com.roamgram.travelDiary.presentation.dto.request.user;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    String name;
    String description;
}
