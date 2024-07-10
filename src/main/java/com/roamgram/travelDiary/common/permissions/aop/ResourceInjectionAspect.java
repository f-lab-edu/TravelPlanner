package com.roamgram.travelDiary.common.permissions.aop;

import com.roamgram.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import com.roamgram.travelDiary.common.permissions.service.ResourcePermissionService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Aspect
@Component
public class ResourceInjectionAspect {
    private final UUID SENTINELUUIDVALUE = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private final ResourcePermissionService resourcePermissionService;

    @Autowired
    public ResourceInjectionAspect(ResourcePermissionService resourcePermissionService) {
        this.resourcePermissionService = resourcePermissionService;
    }

    @Around("@annotation(injectResourceIds)")
    public Object injectResourceIds(ProceedingJoinPoint joinPoint, InjectResourceIds injectResourceIds) throws Throwable {
        Method method = getMethodFromJoinPoint(joinPoint);
        UserResourcePermissionTypes permissionType = injectResourceIds.permissionType();
        String resourceType = injectResourceIds.resourceType();
        List<UUID> resourceIds = resourcePermissionService.getResourceIdsByUserPermissionAndType(permissionType, resourceType);
        if(resourceIds.isEmpty()){
            resourceIds.add(SENTINELUUIDVALUE);
        }
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = getParameterNames(method);

        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals(injectResourceIds.parameterName())) {
                args[i] = resourceIds;
                break;
            }
        }

        return joinPoint.proceed(args);
    }

    private Method getMethodFromJoinPoint(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }

    private String[] getParameterNames(Method method) {
        return Arrays.stream(method.getParameters())
                .map(Parameter::getName)
                .toArray(String[]::new);
    }
}

