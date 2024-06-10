package com.example.travelDiary.common.auth.v2.oauth2;

public interface OAuth2UserInfo {

    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();

}
