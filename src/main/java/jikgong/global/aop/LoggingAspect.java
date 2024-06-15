package jikgong.global.aop;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("within(*..*Controller)")
    public void logMethodParameters(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String methodName = joinPoint.getSignature().getName();
        log.info("Request -------> Method Name: " + methodName);
        log.info("Request -------> Parameters: " + Arrays.toString(args));
    }

    @AfterReturning(pointcut = "within(*..*Controller)", returning = "result")
    public void logMethodResult(Object result) {
        log.info("Response ------->Method Result: " + result);
    }
}
