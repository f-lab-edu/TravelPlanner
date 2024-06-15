package com.example.travelDiary.common.auth.permissions.domain;

import com.example.travelDiary.common.auth.domain.AuthUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourcePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AuthUser user;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Resource resource;

    @Enumerated(EnumType.STRING)
    private UserPermissionTypes permissions;

    // other fields and methods
}



