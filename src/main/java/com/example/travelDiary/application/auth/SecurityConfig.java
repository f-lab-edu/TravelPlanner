package com.example.travelDiary.application.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler successHandler;
    private final TokenService tokenService;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())
                .logout(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/token/**").permitAll()
                        .requestMatchers("/login/**").permitAll()
                        //private로 시작하는 uri는 로그인 필수
                        .anyRequest().authenticated() //나머지 uri는 모든 접근 허용
                ).addFilterBefore(new JwtAuthFilter(tokenService), UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/token/expired") //로그인이 필요한데 로그인을 하지 않았다면 이동할 uri 설정
                        .successHandler(successHandler)
//                        .defaultSuccessUrl("/oauth/loginInfo", true)  //OAuth 구글 로그인이 성공하면 이동할 uri 설정
                        .userInfoEndpoint(userInfo -> userInfo  //로그인 완료 후 회원 정보 받기
                                .userService(customOAuth2UserService)
                        )
                )
//                .addFilterBefore(new JwtAuthFilter(tokenService), UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(new JwtAuthenticationFilter(), OAuth2LoginAuthenticationFilter.class)
                .httpBasic(withDefaults())
                .build(); //로그인 후 받아온 유저 정보
    }
}
