package com.example.travelDiary.common.permissions.aop;

import com.example.travelDiary.domain.IdentifiableResource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CheckAccess {
    Class<? extends IdentifiableResource> resourceType();
    String resourceId();
    String permission();
    boolean isList() default false;
}



