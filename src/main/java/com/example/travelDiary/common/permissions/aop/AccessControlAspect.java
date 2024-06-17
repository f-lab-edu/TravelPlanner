package com.example.travelDiary.common.permissions.aop;

import com.example.travelDiary.common.permissions.service.AccessControlService;
import com.example.travelDiary.domain.IdentifiableResource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.List;
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
        Object[] args = joinPoint.getArgs();
        UUID resourceId = (UUID) new SpelExpressionParser()
                .parseExpression(checkAccess.resourceId())
                .getValue(new StandardEvaluationContext(args));
        String permission = checkAccess.permission();
        Class<? extends IdentifiableResource> resourceType = checkAccess.resourceType();

        if (!accessControlService.hasPermission(resourceType, resourceId,permission)) {
            throw new AccessDeniedException("Access Denied");
        }

        return joinPoint.proceed();
    }

    @Around("@annotation(filterResultsForUser)")
    public Object filterResultsForUser(ProceedingJoinPoint joinPoint, FilterResultsForUser filterResultsForUser) throws Throwable {
        Object result = joinPoint.proceed();
        Class<? extends IdentifiableResource> resourceType = filterResultsForUser.resourceType();

        if (result instanceof Page<?> pageResult) {
            List<?> filteredContent = pageResult.getContent().stream()
                    .filter(resource -> accessControlService.hasPermission(resourceType, ((IdentifiableResource) resource).getId(), "VIEW"))
                    .toList();
            return new PageImpl<>(filteredContent, pageResult.getPageable(), filteredContent.size());
        }

        return result;
    }
}
