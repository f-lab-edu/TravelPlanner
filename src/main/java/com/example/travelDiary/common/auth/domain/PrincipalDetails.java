package com.example.travelDiary.common.auth.domain;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Data
public class PrincipalDetails implements OAuth2User, UserDetails {

    private AuthUser user;
    private Map<String, Object> attributes;

    //for Oauth
    public PrincipalDetails(AuthUser user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    //for normal
    public PrincipalDetails(AuthUser user) {
        this.user = user;
    }

    //oauth2User
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }


    //UserDetails
    @Override
    public String getPassword() {
        return user.getSaltedPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {

                return String.valueOf(user.getRole());
            }
        });
        return collection;
    }

    @Override
    public String getName() {
        return user.getName();
    }
}
