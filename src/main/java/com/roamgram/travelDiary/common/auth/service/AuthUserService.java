package com.roamgram.travelDiary.common.auth.service;

import com.roamgram.travelDiary.common.auth.domain.AuthUser;
import com.roamgram.travelDiary.common.auth.dto.RegistrationRequest;
import com.roamgram.travelDiary.domain.model.user.UserProfile;

public interface AuthUserService {

    AuthUser register(RegistrationRequest registrationRequest);

    void logout(String token);

    AuthUser getCurrentAuthenticatedUser();

    UserProfile getCurrentUser();

    UserProfile toUser(AuthUser authUser);

    AuthUser confirmUserRegistration(String token);
}
