package com.example.travelDiary.common.auth.permissions.aop;

import com.example.travelDiary.common.auth.permissions.domain.UserPermissionTypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CheckAccess {
    String resourceId();
    UserPermissionTypes permissionType();
}
