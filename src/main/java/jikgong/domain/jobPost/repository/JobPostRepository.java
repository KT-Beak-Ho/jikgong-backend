package jikgong.domain.jobPost.repository;

import jikgong.domain.jobPost.entity.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, Long> {
    @Query("select j from JobPost j where j.member.id = :memberId")
    List<JobPost> findByMemberId(@Param("memberId") Long memberId);

    @Query("select j from JobPost j where j.member.id = :memberId and j.endTime < :now")
    List<JobPost> findCompletedJobPostByMemberId(@Param("memberId") Long memberId, @Param("now") LocalDateTime now);

    @Query("select j from JobPost j where j.member.id = :memberId and j.startTime < :now and j.endTime > :now")
    List<JobPost> findInProgressJobPostByMemberId(@Param("memberId") Long memberId, @Param("now") LocalDateTime now);

    @Query("select j from JobPost j where j.member.id = :memberId and j.startTime > :now")
    List<JobPost> findPlannedJobPostByMemberId(@Param("memberId") Long memberId, @Param("now") LocalDateTime now);

}
