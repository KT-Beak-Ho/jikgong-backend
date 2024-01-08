package jikgong.domain.workDay.repository;

import jikgong.domain.workDay.entity.WorkDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkDayRepository extends JpaRepository<WorkDay, Long> {
}
