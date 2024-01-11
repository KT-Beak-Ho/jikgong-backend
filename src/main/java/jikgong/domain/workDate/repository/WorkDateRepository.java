package jikgong.domain.workDate.repository;

import jikgong.domain.workDate.entity.WorkDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface WorkDateRepository extends JpaRepository<WorkDate, Long> {
    @Query("select w from WorkDate w where w.jobPost.id = :jobPostId and w.workDate = :workDate")
    Optional<WorkDate> findByWorkDateAndJobPost(@Param("jobPostId") Long jobPostId, @Param("workDate")LocalDate workDate);
}
