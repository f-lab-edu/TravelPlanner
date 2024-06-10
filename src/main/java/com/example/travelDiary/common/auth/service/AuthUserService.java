package com.example.travelDiary.common.auth.service;

import com.example.travelDiary.common.auth.domain.AuthUser;

public interface AuthUserService {

    public AuthUser register(String username, String password);

}
