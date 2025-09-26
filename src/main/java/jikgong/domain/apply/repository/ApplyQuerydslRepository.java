package jikgong.domain.apply.repository;

import jikgong.domain.apply.dto.worker.ApplyDailyGetRequest;
import jikgong.domain.apply.dto.worker.ApplyGetRequest;
import jikgong.domain.apply.entity.Apply;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ApplyQuerydslRepository {
    List<Apply> findAppliesWithWorkDate(Long workerId, ApplyGetRequest request);
    List<Apply> findAppliesWithWorkDate(Long workerId, LocalDate date);
}
