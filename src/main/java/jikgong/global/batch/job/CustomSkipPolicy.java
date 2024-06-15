package jikgong.global.batch.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomSkipPolicy implements SkipPolicy {
    private int skipCount = 0;

    @Override
    public boolean shouldSkip(Throwable t, long skipCount) {
        if (skipCount > 5) {
            log.error("skip한 item 5개 초과");
            return false; // 최대 Skip 횟수 초과 시 Skip 중지
        }

        log.warn("item 처리 중 예외 발생: {}", t.getMessage());
        this.skipCount++;
        return true;
    }
}
