package com.example.travelDiary.common.auth;

import com.example.travelDiary.common.auth.dto.UserDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends GenericFilterBean {
    private final TokenService tokenService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String qtoken = ((HttpServletRequest)request).getParameter("Authorization");
        log.info(qtoken);
        String token = ((HttpServletRequest)request).getHeader("Authorization");
        log.info(token);

        if (qtoken != null && tokenService.validateToken(qtoken)) {
            String email = tokenService.getUid(qtoken);

            // DB연동을 안했으니 이메일 정보로 유저를 만들어주겠습니다
            UserDto userDto = UserDto.builder()
                    .email(email)
                    .name("이름이에용")
                    .picture("프로필 이미지에요").build();

            Authentication auth = getAuthentication(userDto);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(request, response);
    }

    public Authentication getAuthentication(UserDto member) {
        return new UsernamePasswordAuthenticationToken(member, "",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
