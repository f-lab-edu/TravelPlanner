package com.example.travelDiary.domain.model.user;

import com.example.travelDiary.common.permissions.domain.ResourcePermission;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    private UUID authUserId;

    private String userProfileName;

    private String userDescription;
}
