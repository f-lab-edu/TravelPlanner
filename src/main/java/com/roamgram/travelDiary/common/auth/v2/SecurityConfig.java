package com.roamgram.travelDiary.common.auth.v2;

import com.roamgram.travelDiary.common.auth.GlobalCorsConfig;
import com.roamgram.travelDiary.common.auth.service.PrincipalOauth2Service;
import com.roamgram.travelDiary.common.auth.v2.jwt.JwtAuthenticationFilter;
import com.roamgram.travelDiary.common.auth.v2.oauth2.CustomOAuth2SuccessHandler;
import com.roamgram.travelDiary.common.auth.v2.ratelimiter.RateLimitingFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableWebSecurity
@Slf4j
@Import(GlobalCorsConfig.class)
public class SecurityConfig {
    private final PrincipalOauth2Service principalOauth2Service;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final RateLimitingFilter rateLimitingFilter;

    @Autowired
    public SecurityConfig(PrincipalOauth2Service principalOauth2Service, JwtAuthenticationFilter jwtAuthenticationFilter, CustomOAuth2SuccessHandler customOAuth2SuccessHandler, RateLimitingFilter rateLimitingFilter) {
        this.principalOauth2Service = principalOauth2Service;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customOAuth2SuccessHandler = customOAuth2SuccessHandler;
        this.rateLimitingFilter = rateLimitingFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, InMemoryClientRegistrationRepository clientRegistrationRepository) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/token/**").permitAll()
                        .requestMatchers("/authentication/**").permitAll()
                        .requestMatchers("/", "/login**", "/oauth2/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/h2-console/**",
                                "/actuator",
                                "/actuator/**",
                                "/ws-stomp/**"
                        ).permitAll()  // Allow access to Swagger UI and documentation
                        .requestMatchers("/media-file/complete-upload").permitAll() //aws callback
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(authorize -> authorize
                                .baseUri("/oauth2/authorize-client")
                                .authorizationRequestRepository(authorizationRequestRepository())
                        )
                        .tokenEndpoint(Customizer.withDefaults())
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService()))
                        .successHandler(customOAuth2SuccessHandler)
                )
                .sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; script-src 'self'; object-src 'none';"))
                        .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
                        .contentTypeOptions(Customizer.withDefaults())
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                        .httpStrictTransportSecurity(hstsConfig -> hstsConfig.includeSubDomains(true).maxAgeInSeconds(31536000))
                        .permissionsPolicy(permissions -> permissions.policy("geolocation=(self), microphone=()"))
                )
                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public OidcUserService oidcUserService() {
        OidcUserService oidcUserService = new OidcUserService();
        oidcUserService.setOauth2UserService(principalOauth2Service);
        return oidcUserService;
    }
}
