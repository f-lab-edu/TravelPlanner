package com.example.travelDiary.common.permissions.domain;

import com.example.travelDiary.domain.model.user.UserProfile;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@ToString(exclude = {"permissionsLevel"})
@Slf4j
public class ResourcePermission {

    @Builder
    public ResourcePermission(UUID id, UserProfile userProfile, Resource resource, UserResourcePermissionTypes permissions) {
        this.id = id;
        this.userProfile = userProfile;
        this.resource = resource;
        this.permissions = permissions;
        if (permissions != null) {
            this.permissionsLevel = permissions.getLevel();
        } else {
            throw new IllegalStateException("permissions level is null");
        }
        log.info("ResourcePermission created: {}", this);
    }

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





