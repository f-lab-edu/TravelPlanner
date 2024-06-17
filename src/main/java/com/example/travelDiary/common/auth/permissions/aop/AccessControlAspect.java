package com.example.travelDiary.common.auth.permissions.aop;

import com.example.travelDiary.common.auth.domain.AuthUser;
import com.example.travelDiary.common.auth.permissions.aop.CheckAccess;
import com.example.travelDiary.common.auth.permissions.domain.UserResourcePermissionTypes;
import com.example.travelDiary.common.auth.permissions.service.AccessControlService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
public class AccessControlAspect {

    private final AccessControlService accessControlService;

    @Autowired
    public AccessControlAspect(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    @Around("@annotation(checkAccess)")
    public Object checkAccess(ProceedingJoinPoint joinPoint, CheckAccess checkAccess) throws Throwable {
        // Extract the class type and resource ID from the annotation
        String resourceType = checkAccess.resourceType();
        String resourceIdParam = checkAccess.resourceId();
        UserResourcePermissionTypes permissionType = checkAccess.permissionType();

        // Get the method arguments to find the resourceId
        Object[] args = joinPoint.getArgs();
        UUID resourceId = null;
        for (Object arg : args) {
            if (arg instanceof UUID && arg.toString().equals(resourceIdParam)) {
                resourceId = (UUID) arg;
                break;
            }
        }

        // Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUser user = (AuthUser) authentication.getPrincipal();

        // Check permissions
        boolean hasPermission = accessControlService.hasPermission(user, resourceId, permissionType);
        if (!hasPermission) {
            throw new AccessDeniedException("Access denied");
        }

        return joinPoint.proceed();
    }
}