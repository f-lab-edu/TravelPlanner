package com.roamgram.travelDiary.common.auth.dto;

import lombok.*;

@Data
@Builder
public class JwtToken {
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
