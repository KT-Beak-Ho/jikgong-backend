package jikgong.domain.profit.repository;

import jikgong.domain.profit.entity.Profit;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ProfitQuerydslRepository {
    Optional<Profit> findByWorkDate(Long workerId, LocalDate date);
}
