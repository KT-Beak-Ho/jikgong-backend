package jikgong.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class TimingAdvisor {

    @Pointcut("execution(public * jikgong.domain..*Service.*(..))")
    public void pointcut() {}

    @Around("pointcut()") // 어드바이스 + 포인트컷 설정
    public Object advice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String methodName = proceedingJoinPoint.getSignature().getName();

        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();// 타깃 메소드 호출

        long end = System.currentTimeMillis();
        long runningTime = end - start;

        if (runningTime <= 1000) {
            log.info("[정상 실행] method = {}, 실행시간 = {} ms", methodName, runningTime);
        } else {
            log.error("[!경고] [기준 실행 시간을 초과하였습니다] method = {}, 실행시간 = {} ms", methodName, runningTime);
        }
        return result;
    }
}
