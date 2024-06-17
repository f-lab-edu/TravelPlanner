package com.example.travelDiary.common.permissions.aop;

import com.example.travelDiary.common.auth.domain.AuthUser;

import com.example.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import com.example.travelDiary.common.permissions.service.AccessControlService;
import com.example.travelDiary.domain.IdentifiableResource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
public class FilterResultsAspect {

    private final AccessControlService accessControlService;

    @Autowired
    public FilterResultsAspect(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    @SuppressWarnings("unchecked")
    @Around("@annotation(filterResultsForUser)")
    public Object filterResultsForUser(ProceedingJoinPoint joinPoint, FilterResultsForUser filterResultsForUser) throws Throwable {
        // Proceed with the original method call
        Object result = joinPoint.proceed();

        // Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUser user = (AuthUser) authentication.getPrincipal();
        String permission = filterResultsForUser.permission();
        Class<? extends IdentifiableResource> resourceType = filterResultsForUser.resourceType();

        // Filter the results based on the user's permissions
        if (result instanceof List) {
            List<IdentifiableResource> resources = (List<IdentifiableResource>) result;
            return resources.stream()
                    .filter(resource -> accessControlService.hasPermission(resourceType, resource.getId(), permission))
                    .collect(Collectors.toList());
        } else if (result instanceof Page) {
            Page<IdentifiableResource> resources = (Page<IdentifiableResource>) result;
            List<IdentifiableResource> filteredResources = resources.stream()
                    .filter(resource -> accessControlService.hasPermission(resourceType, resource.getId(), permission))
                    .collect(Collectors.toList());
            return new PageImpl<>(filteredResources, resources.getPageable(), filteredResources.size());
        }

        return result;
    }
}

