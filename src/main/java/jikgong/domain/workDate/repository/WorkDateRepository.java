package jikgong.domain.workDate.repository;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.workDate.entity.WorkDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface WorkDateRepository extends JpaRepository<WorkDate, Long> {
    /**
     * find by id and JobPost
     */
    @Query("select w from WorkDate w where w.jobPost = :jobPostid and w.id = :workDateId")
    Optional<WorkDate> findByIdAndJobPost(@Param("jobPostId") Long jobPostId, @Param("workDateId") Long workDateId);

    /**
     * 일자리 지원
     */
    @Query("select w from WorkDate w where w.jobPost.id = :jobPostId and w.id in :workDateList")
    List<WorkDate> findAllByWorkDateAndJobPost(@Param("jobPostId") Long jobPostId, @Param("workDateList") List<Long> workDateList);

    /**
     * 일자리 제안
     */
    @Query("select w from WorkDate w join fetch w.jobPost p where w.id in :workDateList")
    List<WorkDate> findByIdList(@Param("workDateList") List<Long> workDateIdList);
}
