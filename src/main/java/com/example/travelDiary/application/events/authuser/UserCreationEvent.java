package com.example.travelDiary.application.events.authuser;

import com.example.travelDiary.common.auth.domain.AuthUser;
import lombok.Data;

@Data
public class UserCreationEvent {
    private final AuthUser authUser;
}
