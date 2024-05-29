package com.example.travelDiary.application.auth;

import com.example.travelDiary.application.service.user.UserMutationService;
import com.example.travelDiary.application.auth.dto.OAuthAttributes;
import com.example.travelDiary.application.auth.dto.SessionUser;
import com.example.travelDiary.domain.model.user.Users;
import com.example.travelDiary.repository.persistence.user.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpSession httpSession;
    private final UserMutationService userMutationService;

    @Autowired
    public CustomOAuth2UserService(UserRepository userRepository, HttpSession httpSession, UserMutationService userMutationService) {
        this.userRepository = userRepository;
        this.httpSession = httpSession;
        this.userMutationService = userMutationService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        Users users = saveOrUpdate(attributes);

        httpSession.setAttribute("user", new SessionUser(users));

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(users.getRole().getKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private Users saveOrUpdate(OAuthAttributes attributes) {
        Users users = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> userMutationService.update(entity, attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());

        return userRepository.save(users);
    }
}
