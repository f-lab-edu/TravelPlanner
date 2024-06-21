package com.example.travelDiary.common.permissions.domain;

import com.example.travelDiary.domain.model.user.UserProfile;
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

    public UserResourcePermissionTypes getPermissions() {
        return UserResourcePermissionTypes.fromLevel(this.permissionsLevel);
    }

    public void setPermissions(UserResourcePermissionTypes permissions) {
        this.permissions = permissions;
        this.permissionsLevel = permissions.getLevel();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserResourcePermissionTypes permissions;

    @Column(nullable = false)
    private int permissionsLevel;
}





