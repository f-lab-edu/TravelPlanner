package com.example.travelDiary.common.auth.permissions;

import com.example.travelDiary.common.auth.domain.AuthUser;
import com.example.travelDiary.common.auth.permissions.domain.UserPermissionTypes;
import com.example.travelDiary.common.auth.permissions.service.AccessControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.UUID;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private AccessControlService accessControlService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (targetDomainObject instanceof UUID) {
            AuthUser user = (AuthUser) authentication.getPrincipal();
            UUID resourceId = (UUID) targetDomainObject;
            UserPermissionTypes permissionType = UserPermissionTypes.valueOf((String) permission);
            return accessControlService.hasPermission(user, resourceId, permissionType);
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (targetId instanceof UUID) {
            AuthUser user = (AuthUser) authentication.getPrincipal();
            UUID resourceId = (UUID) targetId;
            UserPermissionTypes permissionType = UserPermissionTypes.valueOf((String) permission);
            return accessControlService.hasPermission(user, resourceId, permissionType);
        }
        return false;
    }
}
