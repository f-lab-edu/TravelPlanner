package com.example.travelDiary.common.permissions.aop;

import com.example.travelDiary.common.permissions.service.AccessControlService;
import com.example.travelDiary.domain.IdentifiableResource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
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
        Method method = getMethodFromJoinPoint(joinPoint);
        Object[] args = joinPoint.getArgs();
        StandardEvaluationContext context = new StandardEvaluationContext(args);

        String[] parameterNames = getParameterNames(method);
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        String permission = checkAccess.permission();
        Class<? extends IdentifiableResource> resourceType = checkAccess.resourceType();

        if (checkAccess.isList()) {
            @SuppressWarnings("unchecked")
            List<?> resourceIds = (List<?>) new SpelExpressionParser()
                    .parseExpression(checkAccess.resourceId())
                    .getValue(context);
            for (Object resourceId : resourceIds) {
                if (!accessControlService.hasPermission(resourceType, (UUID)resourceId, permission)) {
                    throw new AccessDeniedException("Access Denied for resource id: " + resourceId);
                }
            }
        } else {
            UUID resourceId = (UUID) new SpelExpressionParser()
                    .parseExpression(checkAccess.resourceId())
                    .getValue(new StandardEvaluationContext(args));
            if (!accessControlService.hasPermission(resourceType, resourceId, permission)) {
                throw new AccessDeniedException("Access Denied for resource id: " + resourceId);
            }
        }

        return joinPoint.proceed();
    }

    private Method getMethodFromJoinPoint(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        String methodName = joinPoint.getSignature().getName();
        Class<?>[] parameterTypes = Arrays.stream(joinPoint.getArgs())
                .map(Object::getClass)
                .toArray(Class<?>[]::new);
        return joinPoint.getTarget().getClass().getMethod(methodName, parameterTypes);
    }

    private String[] getParameterNames(Method method) {
        // If compiled with -parameters, the names are available directly
        return Arrays.stream(method.getParameters())
                .map(Parameter::getName)
                .toArray(String[]::new);
    }
}
