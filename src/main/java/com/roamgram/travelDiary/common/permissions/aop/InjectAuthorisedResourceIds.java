package com.roamgram.travelDiary.common.permissions.aop;

import com.roamgram.travelDiary.common.permissions.domain.UserResourcePermissionTypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface InjectAuthorisedResourceIds {
    String resourceType();
    String parameterName() default "resourceIds";
    UserResourcePermissionTypes permissionType();
}
