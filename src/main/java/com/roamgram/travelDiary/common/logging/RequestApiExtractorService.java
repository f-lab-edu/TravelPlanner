package com.roamgram.travelDiary.common.logging;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roamgram.travelDiary.common.auth.domain.AuthUser;
import com.roamgram.travelDiary.common.auth.service.AuthUserService;
import com.roamgram.travelDiary.common.logging.domain.RequestApiInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
public class RequestApiExtractorService {

    private final AuthUserService authUserService;

    @Autowired
    public RequestApiExtractorService(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    // Token에서 회원정보 추출
    private void setUser(RequestApiInfo requestApiInfo) {
        final AuthUser userProfile = authUserService.getCurrentAuthenticatedUser();
        requestApiInfo.userId = userProfile.getId();
        requestApiInfo.userName = userProfile.getUsername();
    }

    // Request에서 Header 추출
    private void setHeader(HttpServletRequest request, RequestApiInfo requestApiInfo) {
        final Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final String headerName = headerNames.nextElement();
            requestApiInfo.header.put(headerName, request.getHeader(headerName));
        }
    }

    // Request에서 ipAddress 추출
    private void setIpAddress(HttpServletRequest request, RequestApiInfo requestApiInfo) {
        requestApiInfo.ipAddress = Optional.of(request)
                .map(httpServletRequest -> Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                        .orElse(Optional.ofNullable(request.getHeader("Proxy-Client-IP"))
                                .orElse(Optional.ofNullable(request.getHeader("WL-Proxy-Client-IP"))
                                        .orElse(Optional.ofNullable(request.getHeader("HTTP_CLIENT_IP"))
                                                .orElse(Optional.ofNullable(request.getHeader("HTTP_X_FORWARDED_FOR"))
                                                        .orElse(request.getRemoteAddr())))))).orElse(null);
    }



    // API 정보 추출
    private void setApiInfo(JoinPoint joinPoint, Class clazz, RequestApiInfo requestApiInfo) {
        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final Method method = methodSignature.getMethod();
        final RequestMapping requestMapping = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
        final String baseUrl = requestMapping.value()[0];
        Stream.of(GetMapping.class, PutMapping.class, PostMapping.class, DeleteMapping.class, RequestMapping.class)
                .filter(method::isAnnotationPresent)
                .findFirst()
                .ifPresent(mappingClass -> {
                    final Annotation annotation = method.getAnnotation(mappingClass);
                    try {
                        final String[] methodUrl = (String[])mappingClass.getMethod("value").invoke(annotation);
                        requestApiInfo.method = (mappingClass.getSimpleName().replace("Mapping", "")).toUpperCase();
                        requestApiInfo.url = String.format("%s%s", baseUrl, methodUrl.length > 0 ? "/" + methodUrl[0] : "");
                        requestApiInfo.name = (String)mappingClass.getMethod("name").invoke(annotation);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                });
    }

    // Body와 Parameters 추출
    private void setInputStream(JoinPoint joinPoint, ObjectMapper objectMapper, RequestApiInfo requestApiInfo) {
        try {
            final CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
            final String[] parameterNames = codeSignature.getParameterNames();
            final Object[] args = joinPoint.getArgs();
            for (int i = 0; i < parameterNames.length; i++) {
                if (parameterNames[i].equals("request")) {
                    requestApiInfo.body = objectMapper.convertValue(args[i], new TypeReference<Map<String, String>>(){});
                } else {
                    requestApiInfo.parameters.put(parameterNames[i], objectMapper.writeValueAsString(args[i]));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public RequestApiInfo generateRequestApiInfo (JoinPoint joinPoint, Class clazz, ObjectMapper objectMapper) {

        RequestApiInfo requestApiInfo = new RequestApiInfo();

        try {
            final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            setHeader(request, requestApiInfo);
            setIpAddress(request, requestApiInfo);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            setUser(requestApiInfo);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            setApiInfo(joinPoint, clazz, requestApiInfo);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            setInputStream(joinPoint, objectMapper, requestApiInfo);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return requestApiInfo;
    }



}
