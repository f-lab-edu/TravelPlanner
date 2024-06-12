package com.example.travelDiary.common.auth.service;

import com.example.travelDiary.common.auth.domain.AuthUser;
import com.example.travelDiary.common.auth.domain.PrincipalDetails;
import com.example.travelDiary.common.auth.domain.Role;
import com.example.travelDiary.common.auth.repository.AuthUserRepository;
import com.example.travelDiary.common.auth.v2.oauth2.GoogleUserInfo;
import com.example.travelDiary.common.auth.v2.oauth2.OAuth2UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j
public class PrincipalOauth2Service extends DefaultOAuth2UserService {

    private final PasswordEncoder passwordEncoder;
    private final AuthUserRepository authUserRepository;

    @Autowired
    public PrincipalOauth2Service(PasswordEncoder bCryptPasswordEncoder, AuthUserRepository authUserRepository) {
        this.passwordEncoder = bCryptPasswordEncoder;
        this.authUserRepository = authUserRepository;
    }

    //구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
    //함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        //"registraionId" 로 어떤 OAuth 로 로그인 했는지 확인 가능(google,naver등)
        log.info("getClientRegistration: {}", userRequest.getClientRegistration());
        log.info("getAccessToken: {}", userRequest.getAccessToken().getTokenValue());
        log.info("getAttributes: {}", super.loadUser(userRequest).getAttributes());
        //구글 로그인 버튼 클릭 -> 구글 로그인창 -> 로그인 완료 -> code를 리턴(OAuth-Clien라이브러리가 받아줌) -> code를 통해서 AcssToken요청(access토큰 받음)
        // => "userRequest"가 감고 있는 정보
        //회원 프로필을 받아야하는데 여기서 사용되는것이 "loadUser" 함수이다 -> 구글 로 부터 회원 프로필을 받을수 있다.


        /**
         *  OAuth 로그인 회원 가입
         */
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2UserInfo oAuth2UserInfo = null;
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else{
            System.out.println("지원하지 않은 로그인 서비스 입니다.");
        }

        String provider = oAuth2UserInfo.getProvider(); //google , naver, facebook etc
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider + "_" + providerId;
        String password =  passwordEncoder.encode("테스트"); //중요하지 않음 그냥 패스워드 암호화 하
        String email = oAuth2UserInfo.getEmail();
        String name = oAuth2UserInfo.getName();
        Role role = Role.USER;

        Optional<AuthUser> userEntityOptional = authUserRepository.findByUsername(username);

        AuthUser userEntity = userEntityOptional.orElseGet(() -> {
            AuthUser newUser = AuthUser.builder()
                    .username(username)
                    .name(name)
                    .saltedPassword(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .createdAt(Instant.now())
                    .build();

            return authUserRepository.save(newUser);
        });

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
