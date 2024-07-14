package com.roamgram.travelDiary.common.permissions.aop;

import com.roamgram.travelDiary.common.permissions.domain.UserResourcePermissionTypes;
import com.roamgram.travelDiary.common.permissions.service.ResourcePermissionService;
import com.roamgram.travelDiary.common.permissions.service.ResourceService;
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
    private final ResourceService resourceService;
    private final ResourcePermissionService resourcePermissionService;

    @Autowired
    public ResourceInjectionAspect(ResourceService resourceService, ResourcePermissionService resourcePermissionService) {
        this.resourceService = resourceService;
        this.resourcePermissionService = resourcePermissionService;
    }

    @Around("@annotation(injectAuthorisedResourceIds)")
    public Object injectAuthorisedResourceIds(ProceedingJoinPoint joinPoint, InjectAuthorisedResourceIds injectAuthorisedResourceIds) throws Throwable {
        Method method = getMethodFromJoinPoint(joinPoint);
        UserResourcePermissionTypes permissionType = injectAuthorisedResourceIds.permissionType();
        String resourceType = injectAuthorisedResourceIds.resourceType();
        List<UUID> resourceIds = resourcePermissionService.getResourceIdsByUserPermissionAndType(permissionType, resourceType);
        if(resourceIds.isEmpty()){
            resourceIds.add(SENTINELUUIDVALUE);
        }
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = getParameterNames(method);

        findAndFillListParameterWithValues(parameterNames, injectAuthorisedResourceIds.parameterName(), args, resourceIds);

        return joinPoint.proceed(args);
    }

    @Around("@annotation(injectPublicResourceIds)")
    public Object injectPublicResourceIds(ProceedingJoinPoint joinPoint, InjectPublicResourceIds injectPublicResourceIds) throws Throwable {
        Method method = getMethodFromJoinPoint(joinPoint);
        String resourceType = injectPublicResourceIds.resourceType();
        List<UUID> resourceIds = resourceService.getPublicResourceIds(resourceType);
        if(resourceIds.isEmpty()){
            resourceIds.add(SENTINELUUIDVALUE);
        }
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = getParameterNames(method);

        findAndFillListParameterWithValues(parameterNames, injectPublicResourceIds.parameterName(), args, resourceIds);

        return joinPoint.proceed(args);
    }

    private static void findAndFillListParameterWithValues(String[] parameterNames, String injectAuthorisedResourceIds, Object[] args, List<UUID> resourceIds) {
        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals(injectAuthorisedResourceIds)) {
                args[i] = resourceIds;
                break;
            }
        }
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

