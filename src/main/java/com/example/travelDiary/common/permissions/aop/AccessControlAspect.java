package com.example.travelDiary.common.permissions.aop;

import com.example.travelDiary.common.permissions.service.AccessControlService;
import com.example.travelDiary.domain.IdentifiableResource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
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

    @Around("@annotation(CheckAccess) || @annotation(CheckAccesses)")
    public Object checkAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Method method = getMethodFromJoinPoint(joinPoint);
        CheckAccesses checkAccessesAnnotation = method.getAnnotation(CheckAccesses.class);
        CheckAccess[] checkAccessArray = checkAccessesAnnotation != null ? checkAccessesAnnotation.value() : new CheckAccess[]{method.getAnnotation(CheckAccess.class)};

        for (CheckAccess checkAccess : checkAccessArray) {
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
                        .parseExpression(checkAccess.spelResourceId())
                        .getValue(context);
                for (Object resourceId : resourceIds) {
                    if (!accessControlService.hasPermission(resourceType, (UUID) resourceId, permission)) {
                        throw new AccessDeniedException("Access Denied for resource id: " + resourceId);
                    }
                }
            } else {
                UUID resourceId = (UUID) new SpelExpressionParser()
                        .parseExpression(checkAccess.spelResourceId())
                        .getValue(context);
                if (!accessControlService.hasPermission(resourceType, resourceId, permission)) {
                    throw new AccessDeniedException("Access Denied for resource id: " + resourceId);
                }
            }
        }

        return joinPoint.proceed();
    }

    protected Method getMethodFromJoinPoint(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        return getMethod(joinPoint);
    }

    @NotNull
    private Method getMethod(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        String methodName = joinPoint.getSignature().getName();
        Class<?>[] parameterTypes = Arrays.stream(joinPoint.getArgs())
                .map(arg -> {
                    // Convert any type of List to List.class
                    if (arg instanceof List) {
                        return List.class;
                    }
                    return arg.getClass();
                })
                .toArray(Class<?>[]::new);
        return joinPoint.getTarget().getClass().getMethod(methodName, parameterTypes);
    }

    private String[] getParameterNames(Method method) {
        // If compiled with -parameters, the names are available directly
        return Arrays.stream(method.getParameters())
                .map(Parameter::getName)
                .toArray(String[]::new);
    }

    private Object[] convertImmutableListsToMutable(Object[] args) {
        Object[] modifiedArgs = Arrays.copyOf(args, args.length);

        for (int i = 0; i < modifiedArgs.length; i++) {
            if (modifiedArgs[i] instanceof List && modifiedArgs[i].getClass().getName().contains("ImmutableCollections")) {
                modifiedArgs[i] = new ArrayList<>((List<?>) modifiedArgs[i]);
            }
        }

        return modifiedArgs;
    }
}
