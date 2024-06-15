package jikgong.domain.workdate.entity;

import jikgong.domain.etc.workDate.WorkDateTestService;
import jikgong.domain.workdate.repository.WorkDateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class WorkDateTest {

    @Autowired
    private WorkDateRepository workDateRepository;
    @Autowired
    private WorkDateTestService workDateTestService;

    @BeforeEach
    public void setUp() {
        WorkDate workDate = WorkDate.builder()
                .date(LocalDate.now())
                .recruitNum(100)
                .build();
        workDateRepository.saveAndFlush(workDate);

        // 엔티티가 제대로 저장되었는지 확인합니다.
        WorkDate savedWorkDate = workDateRepository.findById(workDate.getId()).orElseThrow(() -> new IllegalStateException("Entity not found after save"));

        System.out.println(savedWorkDate.getId());
    }

    @Test
    public void 동시성_테스트() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    workDateTestService.plusRegisteredNum(1L);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        WorkDate updatedWorkDate = workDateRepository.findById(1L).get();
        assertThat(updatedWorkDate.getRegisteredNum()).isEqualTo(threadCount);
    }
}