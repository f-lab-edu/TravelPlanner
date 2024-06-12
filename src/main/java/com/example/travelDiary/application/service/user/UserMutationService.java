package com.example.travelDiary.application.service.user;

import com.example.travelDiary.domain.model.user.Users;
import org.springframework.stereotype.Service;

@Service
public class UserMutationService {
    public Users update(Users users, String name, String picture) {

        users.setName(name);
        users.setPicture(picture);

        return users;

    }
}
