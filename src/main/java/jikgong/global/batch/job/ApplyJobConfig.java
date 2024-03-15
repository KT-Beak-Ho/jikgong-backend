package jikgong.global.batch.job;

import jakarta.persistence.EntityManagerFactory;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.member.entity.Member;
import jikgong.domain.notification.entity.NotificationType;
import jikgong.domain.notification.service.NotificationService;
import jikgong.domain.workDate.entity.WorkDate;
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

    @Bean
    public Job applyProcessJob(JobRepository jobRepository, Step applyProcessStep) {
        return new JobBuilder("applyProcessJob", jobRepository)
                .start(applyProcessStep)
                .build();
    }

    @Bean
    public Step applyProcessStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("applyProcessStep", jobRepository)
                .<Apply, Apply>chunk(100, transactionManager)
                .reader(applyReader())
                .processor(applyProcessor())
                .writer(applyWriter())
                .build();
    }

    @Bean
    public ItemReader<Apply> applyReader() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        JpaPagingItemReader<Apply> reader = new JpaPagingItemReader<>() {
            @Override
            public int getPage() {
                return 0;
            }
        };
        reader.setName("applyReader");
        reader.setPageSize(100);
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("select a from Apply a join fetch a.workDate w join fetch a.member m join fetch a.workDate.jobPost j where w.workDate <= :tomorrow and a.status = 'PENDING'");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("tomorrow", tomorrow);
        reader.setParameterValues(parameters);
        return reader;
    }
//        return new RepositoryItemReaderBuilder<Apply>()
//                .name("applyReader")
//                .repository(applyRepository)
//                .methodName("findNeedToCancel")
//                .pageSize(1)
//                .arguments(List.of(tomorrow))
//                .sorts(Collections.singletonMap("id", Sort.Direction.ASC)) // 필수
//                .build();
//    }

    @Bean
    public ItemProcessor<Apply, Apply> applyProcessor() {
        return apply -> {
            Member member = apply.getMember();
            WorkDate workDate = apply.getWorkDate();
            JobPost jobPost = workDate.getJobPost();

            // 알림 전송
            String content = workDate.getWorkDate().toString() + ", " + jobPost.getTitle() + " 현장 지원이 자동 취소되었습니다.";
            String url = "/api/worker/job-post/" + jobPost.getId();
            notificationService.saveNotification(member.getId(), NotificationType.APPLY_CANCEL, content, url);

            // apply status 업데이트
            apply.updateStatus(ApplyStatus.CANCELED, LocalDateTime.now());

            return apply;
        };
    }

    @Bean
    public ItemWriter<Apply> applyWriter() {
        return new JpaItemWriterBuilder<Apply>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
