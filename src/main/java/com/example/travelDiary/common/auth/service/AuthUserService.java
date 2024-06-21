package com.example.travelDiary.common.auth.service;

import com.example.travelDiary.common.auth.domain.AuthUser;
import com.example.travelDiary.common.auth.dto.RegistrationRequest;
import com.example.travelDiary.domain.model.user.UserProfile;

public interface AuthUserService {

    public AuthUser register(RegistrationRequest registrationRequest);

    void logout(String token);

    AuthUser getCurrentAuthenticatedUser();

    UserProfile getCurrentUser();

    UserProfile toUser(AuthUser authUser);
}
