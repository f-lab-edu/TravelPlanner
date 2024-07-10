package com.roamgram.travelDiary.common.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private final ObjectMapper objectMapper;

    // 모든 컨트롤러 && NotLogging 어노테이션 미설정 시 로그 수집
    @Pointcut("within(*..*Controller) && !@annotation(com.project.sample.aop.NotLogging)")
    public void onRequest() {}

    @Around("onRequest()")
    public Object requestLogging(ProceedingJoinPoint joinPoint) throws Throwable {

        // API 요청 정보
        final RequestApiInfo apiInfo = new RequestApiInfo(joinPoint, joinPoint.getTarget().getClass(), objectMapper);

        // 로그 정보
        final LogInfo logInfo = new LogInfo(
                apiInfo.getUrl(),
                apiInfo.getName(),
                apiInfo.getMethod(),
                apiInfo.getHeader(),
                objectMapper.writeValueAsString(apiInfo.getParameters()),
                objectMapper.writeValueAsString(apiInfo.getBody()),
                apiInfo.getIpAddress(),
                apiInfo.getUserId(),
                apiInfo.getUserName()
        );

        try {
            final Object result = joinPoint.proceed(joinPoint.getArgs());

            // Method가 Get이 아닌 로그만 수집
            if (!logInfo.getMethod().equals("GET")) {
                final String logMessage = objectMapper.writeValueAsString(Map.entry("logInfo", logInfo));
                logger.info(logMessage);
            }

            return result;
        } catch (Exception e) {
            final StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            final String exceptionAsString = sw.toString();

            // 발생 Exception 설정
            logInfo.setException(exceptionAsString);
            final String logMessage = objectMapper.writeValueAsString(logInfo);
            logger.error(logMessage);

            throw e;
        }
    }

}