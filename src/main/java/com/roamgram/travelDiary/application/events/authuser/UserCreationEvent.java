package com.roamgram.travelDiary.application.events.authuser;

import com.roamgram.travelDiary.common.auth.domain.AuthUser;
import lombok.Data;

@Data
public class UserCreationEvent {
    private final AuthUser authUser;
}
