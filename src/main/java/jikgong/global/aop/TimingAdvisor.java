package jikgong.global.aop;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import jikgong.global.slack.SlackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Aspect
@Slf4j
public class TimingAdvisor {

    private final SlackService slackService;

    @Pointcut("execution(public * jikgong.domain..*Service.*(..))")
    public void pointcut() {
    }

    @Around("pointcut()") // 어드바이스 + 포인트컷 설정
    public Object advice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());

        String className = proceedingJoinPoint.getSignature().getDeclaringTypeName();
        String methodName = proceedingJoinPoint.getSignature().getName();

        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed(); // 타깃 메소드 호출
        long end = System.currentTimeMillis();
        long runningTime = end - start;

        if (runningTime > 5000) {
            String message = String.format("[!경고] [기준 실행 시간을 초과하였습니다] 시간: %s, 클래스: %s, 메서드: %s, 실행 시간: %dms",
                currentTime, className, methodName, runningTime);
            log.warn(message);
            // Slack 메시지 전송
            HashMap<String, String> data = new HashMap<>();
            data.put("초과 시각", currentTime);
            data.put("초과 클래스", className);
            data.put("초과 메서드", methodName);
            data.put("실행 시간", runningTime + "ms");
            slackService.sendMessage("경고: 실행 시간 초과", data);
        }
        return result;
    }
}
