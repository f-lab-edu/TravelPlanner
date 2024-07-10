package com.roamgram.travelDiary.common.permissions.dto;

import com.roamgram.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import lombok.Data;

import java.util.UUID;

@Data
public class ResourcePermissionUpdateRequest {
    private UUID resourceId;
    private UserResourcePermissionTypes permissions;
}

