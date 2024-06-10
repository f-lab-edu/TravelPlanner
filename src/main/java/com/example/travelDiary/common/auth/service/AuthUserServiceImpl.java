package com.example.travelDiary.common.auth.service;


import com.example.travelDiary.common.auth.domain.AuthUser;
import com.example.travelDiary.common.auth.repository.AuthUserRepository;
import com.example.travelDiary.common.auth.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthUserServiceImpl implements AuthUserService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthUserServiceImpl(AuthUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authUserRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthUser register(String username, String password) {
        if (authUserRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        AuthUser user = new AuthUser();
        user.setUsername(username);
        user.setSaltedPassword(passwordEncoder.encode(password));
        user.setRole(Role.valueOf("ROLE_USER"));

        return authUserRepository.save(user);
    }

}

