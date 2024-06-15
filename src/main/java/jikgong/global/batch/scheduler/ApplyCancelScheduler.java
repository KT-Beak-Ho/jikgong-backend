package jikgong.global.batch.scheduler;

import java.util.HashMap;
import java.util.Map;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplyCancelScheduler {

    private final JobLauncher jobLauncher;
    private final Job applyJobConfig;

    @Scheduled(cron = "0 05 00 * * *", zone = "Asia/Seoul") // 초, 분, 시, 일, 월, 주, 년(생략 가능)
//    @Scheduled(fixedDelay=10000) // 단위: ms
    public void applyCancel() {
        log.info("스케줄러 실행");
        // job parameter 설정
        Map<String, JobParameter<?>> confMap = new HashMap<>();
        confMap.put("time", new JobParameter(System.currentTimeMillis(), Long.class));
        JobParameters jobParameters = new JobParameters(confMap);
        //JobParamter의 역할은 반복해서 실행되는 Job의 유일한 ID이다.
        //Job Parameter에 동일한 값이 세팅되면 두번째부터 실행이 안되기 때문이다.

        try {
            jobLauncher.run(applyJobConfig, jobParameters);
        } catch (Exception e) {
            throw new JikgongException(ErrorCode.SCHEDULER_INTERNAL_ERROR);
        }
    }
}
