package com.example.travelDiary.common.auth.dto;

import lombok.Data;

@Data
public class RegistrationRequest {
    private String username;
    private String password;
    private String name;
    private String email;
}
