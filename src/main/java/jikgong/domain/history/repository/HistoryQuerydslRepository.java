package jikgong.domain.history.repository;

import jikgong.domain.history.entity.History;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HistoryQuerydslRepository {
    Optional<History> findByWorkDate(Long workerId, LocalDate date);
}
