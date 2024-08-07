package com.roamgram.travelDiary.common.permissions;

import com.roamgram.travelDiary.common.auth.domain.AuthUser;
import com.roamgram.travelDiary.common.auth.domain.PrincipalDetails;
import com.roamgram.travelDiary.common.permissions.service.AccessControlService;
import com.roamgram.travelDiary.domain.IdentifiableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.UUID;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    private final AccessControlService accessControlService;

    @Autowired
    public CustomPermissionEvaluator(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (targetDomainObject instanceof IdentifiableResource) {
            IdentifiableResource resource = (IdentifiableResource) targetDomainObject;
            AuthUser currentUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
            return accessControlService.hasPermission(resource.getClass(), resource.getId(), (String) permission);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        try {
            Class<? extends IdentifiableResource> resourceType = (Class<? extends IdentifiableResource>) Class.forName(targetType);
            UUID resourceId = (UUID) targetId;
            AuthUser currentUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
            return accessControlService.hasPermission(resourceType, resourceId, (String) permission);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
