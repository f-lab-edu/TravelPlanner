package com.example.travelDiary.common.auth.permissions.dto;

import com.example.travelDiary.common.auth.permissions.domain.UserResourcePermissionTypes;
import lombok.Data;

import java.util.UUID;

@Data
public class ResourcePermissionUpdateRequest {
    private UUID resourceId;
    private UserResourcePermissionTypes permissions;
}

