package com.search.common.log.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
@RequiredArgsConstructor
public class ExceptionLogAspect {
    private final Logger logger = LoggerFactory.getLogger(ExceptionLogAspect.class);

    @AfterThrowing(pointcut = "within(com.*)", throwing = "exception")
    public void logException(JoinPoint joinPoint, Throwable exception) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        printErrorLog(exception, className, methodName);

        throw exception;
    }

    private void printErrorLog(Throwable exception, String className, String methodName) {
        logger.error("Exception in {}.{}() with cause = {}", className, methodName,
                exception.getCause() != null ? exception.getCause() : "NULL");
    }

}
