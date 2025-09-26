package jikgong.domain.history.repository;

import jikgong.domain.history.entity.History;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HistoryQuerydslRepository {
    Optional<History> findByWorkDateForWorker(Long workerId, LocalDate date);
    List<History> findByWorkDateId(Long workDateId);
}
