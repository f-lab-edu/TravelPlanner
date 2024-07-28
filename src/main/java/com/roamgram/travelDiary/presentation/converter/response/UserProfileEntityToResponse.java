package com.roamgram.travelDiary.presentation.converter.response;

import com.roamgram.travelDiary.domain.model.user.UserProfile;
import com.roamgram.travelDiary.presentation.dto.response.user.UserProfileResponse;
import org.springframework.core.convert.converter.Converter;

public class UserProfileEntityToResponse implements Converter<UserProfile, UserProfileResponse> {

    @Override
    public UserProfileResponse convert(UserProfile source) {
        UserProfileResponse response = new UserProfileResponse();
        if(source.getId() != null) {
            response.setId(source.getId());
        }

        if(source.getUserProfileName() != null) {
            response.setUserProfileName(source.getUserProfileName());
        }

        if(source.getUserDescription() != null) {
            response.setUserDescription(source.getUserDescription());
        }

        return response;
    }
}
