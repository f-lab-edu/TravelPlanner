package com.example.travelDiary.application.service.user;

import com.example.travelDiary.common.auth.domain.AuthUser;
import org.springframework.stereotype.Service;

@Service
public class UserMutationService {
    public AuthUser update(AuthUser authUser, String name, String picture) {

        authUser.setName(name);
        authUser.setPicture(picture);

        return authUser;

    }
}
