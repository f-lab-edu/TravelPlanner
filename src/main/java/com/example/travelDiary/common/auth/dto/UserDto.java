package com.example.travelDiary.common.auth.dto;

import com.example.travelDiary.domain.model.user.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@Builder
public class UserDto implements Serializable {
    private String name;
    private String email;
    private String picture;

    public UserDto(OAuth2User users) {
        this.name = users.getName();
        this.email = users.getAttribute("email");
        this.picture = users.getAttribute("picture");
    }
}



