package com.example.travelDiary.common.auth.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    private String username;

    private String saltedPassword;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    private String picture;

    private Instant createdAt;

    private String provider;

    private String providerId;

    @Enumerated(EnumType.STRING)
    private ApplicationPermits applicationPermits;
}
