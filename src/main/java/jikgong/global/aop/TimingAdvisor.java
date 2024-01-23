package jikgong.global.aop;

import jikgong.global.slack.SlackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
@Aspect
@Slf4j
public class TimingAdvisor {

    private final SlackService slackService;

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
            String message = "[!경고] [기준 실행 시간을 초과하였습니다] method = " + methodName + ", 실행시간 = " + runningTime + "ms";
            log.error(message);
            // Slack 메시지 전송
            HashMap<String, String> data = new HashMap<>();
            data.put("경고 내용", message);
            slackService.sendMessage("경고: 실행 시간 초과", data);
        }
        return result;
    }
}
