package jikgong.global.batch.job;

import jakarta.persistence.EntityManagerFactory;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.member.entity.Member;
import jikgong.domain.notification.entity.NotificationType;
import jikgong.domain.notification.service.NotificationService;
import jikgong.domain.workDate.entity.WorkDate;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplyJobConfig {
    private final NotificationService notificationService;
    private final EntityManagerFactory entityManagerFactory;

    private static final int CHUNK_SIZE = 3;

    @Bean
    public Job applyProcessJob(JobRepository jobRepository, Step applyProcessStep) {
        return new JobBuilder("applyProcessJob", jobRepository)
                .start(applyProcessStep)
                .build();
    }

    @Bean
    public Step applyProcessStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("applyProcessStep", jobRepository)
                .<Apply, Apply>chunk(CHUNK_SIZE, transactionManager)
                .reader(applyReader())
                .processor(applyProcessor())
                .writer(applyWriter())
                .faultTolerant()
                .retryPolicy(retryPolicy())
                .skipPolicy(new CustomSkipPolicy()) // Custom Skip 정책 설정
                .build();
    }

    @Bean
    public ItemReader<Apply> applyReader() {
        LocalDate now = LocalDate.now();
        JpaPagingItemReader<Apply> reader = new JpaPagingItemReader<>() {
            @Override
            public int getPage() {
                return 0;
            }
        };
        reader.setName("applyReader");
        reader.setPageSize(100);
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("select a from Apply a join fetch a.workDate w join fetch a.member m join fetch a.workDate.jobPost j where w.date < :now and a.status = 'PENDING'");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("now", now);
        reader.setParameterValues(parameters);
        return reader;
    }

    @Bean
    public ItemProcessor<Apply, Apply> applyProcessor() {
        return apply -> {
            // 재시도 정책
            RetryTemplate retryTemplate = retryTemplate();

            return retryTemplate.execute(context -> {
                if (context.getRetryCount() > 0) {
                    log.warn("배치 작업 재시도. apply id: {}, 시도 횟수: {}", apply.getId(), context.getRetryCount());
                }
                Member member = apply.getMember();
                WorkDate workDate = apply.getWorkDate();
                JobPost jobPost = workDate.getJobPost();

                // 알림 전송
                String content = workDate.getDate().toString() + ", " + jobPost.getTitle() + " 현장 지원이 자동 취소되었습니다.";
                String url = "/api/worker/job-post/" + jobPost.getId();
                notificationService.saveNotification(member.getId(), NotificationType.APPLY_CANCEL, content, url);

                // apply status 업데이트
                apply.updateStatus(ApplyStatus.CANCELED, LocalDateTime.now());

                return apply;
            }, context -> {
                // 재시도 실패 후 처리
                log.error("모든 재시도 실패. 실패한 Apply ID: {}", apply.getId());
                return null;
            });
        };
    }

    @Bean
    public ItemWriter<Apply> applyWriter() {
        return new JpaItemWriterBuilder<Apply>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    // RetryTemplate 빈 생성 (재시도 정책 설정)
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(5000); // 재시도 간 5초 대기
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3); // 최대 재시도 횟수 3회
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

    // SimpleRetryPolicy 빈 생성 (최대 재시도 횟수 3회)
    @Bean
    public SimpleRetryPolicy retryPolicy() {
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        return retryPolicy;
    }
}