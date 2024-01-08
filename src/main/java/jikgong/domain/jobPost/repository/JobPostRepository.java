package jikgong.domain.jobPost.repository;

import jikgong.domain.jobPost.entity.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, Long> {
    @Query("select j from JobPost j where j.member.id = :memberId and j.isTemporary = true")
    List<JobPost> findTemporaryJobPostByMemberId(@Param("memberId") Long memberId);

    @Query("select j from JobPost j where j.member.id = :memberId and j.endDate < :now and j.isTemporary = false")
    List<JobPost> findCompletedJobPostByMemberId(@Param("memberId") Long memberId, @Param("now") LocalDate now);

    @Query("select j from JobPost j where j.member.id = :memberId and j.startDate < :now and j.endDate > :now and j.isTemporary = false")
    List<JobPost> findInProgressJobPostByMemberId(@Param("memberId") Long memberId, @Param("now") LocalDate now);

    @Query("select j from JobPost j where j.member.id = :memberId and j.startDate > :now and j.isTemporary = false")
    List<JobPost> findPlannedJobPostByMemberId(@Param("memberId") Long memberId, @Param("now") LocalDate now);
}
