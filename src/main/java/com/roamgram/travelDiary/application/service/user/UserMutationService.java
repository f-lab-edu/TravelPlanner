package com.roamgram.travelDiary.application.service.user;

import com.roamgram.travelDiary.common.auth.domain.AuthUser;
import org.springframework.stereotype.Service;

@Service
public class UserMutationService {
    public AuthUser update(AuthUser authUser, String name, String picture) {

        authUser.setName(name);
        authUser.setPicture(picture);

        return authUser;

    }
}
