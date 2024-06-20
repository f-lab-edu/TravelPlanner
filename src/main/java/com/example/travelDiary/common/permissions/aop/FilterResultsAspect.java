package com.example.travelDiary.common.permissions.aop;

import com.example.travelDiary.common.permissions.service.AccessControlService;
import com.example.travelDiary.domain.IdentifiableResource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Around("@annotation(filterResultsForUser)")
    public Object filterResultsForUser(ProceedingJoinPoint joinPoint, FilterResultsForUser filterResultsForUser) throws Throwable {
        Object result = joinPoint.proceed();
        String permission = filterResultsForUser.permission();
        Class<? extends IdentifiableResource> resourceType = filterResultsForUser.resourceType();

        if (result instanceof List) {
            List<?> resources = (List<?>) result;
            List<?> filteredResources = resources.stream()
                    .filter(resource -> accessControlService.hasPermission(resourceType, ((IdentifiableResource) resource).getId(), permission))
                    .collect(Collectors.toList());
            return filteredResources;
        } else if (result instanceof Page) {
            Page<?> resources = (Page<?>) result;
            List<?> filteredResources = resources.getContent().stream()
                    .filter(resource -> accessControlService.hasPermission(resourceType, ((IdentifiableResource) resource).getId(), permission))
                    .collect(Collectors.toList());
            return new PageImpl<>(filteredResources, resources.getPageable(), filteredResources.size());
        }

        return result;
    }
}

