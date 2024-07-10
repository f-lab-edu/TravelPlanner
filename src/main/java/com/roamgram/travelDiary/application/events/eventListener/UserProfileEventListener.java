package com.roamgram.travelDiary.application.events.eventListener;

import com.roamgram.travelDiary.application.events.authuser.UserCreationEvent;
import com.roamgram.travelDiary.common.auth.domain.AuthUser;
import com.roamgram.travelDiary.domain.model.user.UserProfile;
import com.roamgram.travelDiary.repository.persistence.user.UserProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserProfileEventListener {
    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserProfileEventListener(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @EventListener
    public void userCreatedEvent(UserCreationEvent event) {
        AuthUser authUser = event.getAuthUser();
        log.info("Received UserCreationEvent for user: {}", authUser.getUsername());
        if(userProfileRepository.findByAuthUserId(authUser.getId()).isEmpty()) {
            UserProfile userProfile = UserProfile
                    .builder()
                    .userProfileName(authUser.getUsername())
                    .userDescription("new User")
                    .authUserId(authUser.getId())
                    .build();
            userProfileRepository.save(userProfile);
            log.info("UserProfile created for user: {}", authUser.getUsername());
        }
        else {
            log.info("UserProfile already exists for user: {}", authUser.getUsername());
        }
    }
}
