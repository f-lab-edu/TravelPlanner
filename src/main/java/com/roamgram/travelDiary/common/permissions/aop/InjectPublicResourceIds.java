package com.roamgram.travelDiary.common.permissions.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface InjectPublicResourceIds {
    String resourceType();
    String parameterName() default "resourceIds";
}
