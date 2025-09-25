package jikgong.domain.apply.repository;

import jikgong.domain.apply.dto.worker.ApplyStatusGetRequest;
import jikgong.domain.apply.entity.Apply;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplyQuerydslRepository {
    List<Apply> findAppliesWithWorkDate(Long workerId, ApplyStatusGetRequest request);
}
