package com.example.travelDiary.common.auth.permissions.aop;

import com.example.travelDiary.common.auth.domain.AuthUser;
import com.example.travelDiary.common.auth.permissions.repository.ResourceRepository;
import com.example.travelDiary.common.auth.permissions.service.AccessControlService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
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
    private final ResourceRepository resourceRepository;

    @Autowired
    public AccessControlAspect(AccessControlService accessControlService, ResourceRepository resourceRepository) {
        this.accessControlService = accessControlService;
        this.resourceRepository = resourceRepository;
    }

    @Around("@annotation(CheckAccess)")
    public Object checkAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        CheckAccess checkAccess = signature.getMethod().getAnnotation(CheckAccess.class);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUser user = (AuthUser) authentication.getPrincipal();

        // Assuming the first parameter is the resource ID (UUID)
        Object[] args = joinPoint.getArgs();
        UUID resourceId = (UUID) args[0];

        boolean hasPermission = accessControlService.hasPermission(
                user, resourceId, checkAccess.permissionType());

        if (!hasPermission) {
            throw new AccessDeniedException("Access denied");
        }

        return joinPoint.proceed();
    }
}

