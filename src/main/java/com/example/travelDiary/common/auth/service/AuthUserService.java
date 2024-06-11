package com.example.travelDiary.common.auth.service;

import com.example.travelDiary.common.auth.domain.AuthUser;
import com.example.travelDiary.common.auth.dto.RegistrationRequest;

public interface AuthUserService {

    public AuthUser register(RegistrationRequest registrationRequest);

}
