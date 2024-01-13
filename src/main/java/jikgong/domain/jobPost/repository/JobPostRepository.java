package jikgong.domain.jobPost.repository;

import jikgong.domain.jobPost.entity.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, Long> {
    @Query("select j from JobPost j where j.member.id = :memberId and j.isTemporary = true")
    Page<JobPost> findTemporaryJobPostByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @Query("select j from JobPost j where j.member.id = :memberId and j.endDate < :now and j.isTemporary = false and j.project.id = :projectId")
    List<JobPost> findCompletedJobPostByMemberAndProject(@Param("memberId") Long memberId, @Param("now") LocalDate now, @Param("projectId") Long projectId, Pageable pageable);

    @Query("select j from JobPost j where j.member.id = :memberId and j.startDate < :now and j.endDate > :now and j.isTemporary = false and j.project.id = :projectId")
    List<JobPost> findInProgressJobPostByMemberAndProject(@Param("memberId") Long memberId, @Param("now") LocalDate now, @Param("projectId") Long projectId, Pageable pageable);

    @Query("select j from JobPost j where j.member.id = :memberId and j.startDate > :now and j.isTemporary = false and j.project.id = :projectId")
    List<JobPost> findPlannedJobPostByMemberAndProject(@Param("memberId") Long memberId, @Param("now") LocalDate now, @Param("projectId") Long projectId, Pageable pageable);

    @Query("select j from JobPost j where j.member.id = :memberId and j.id = :jobPostId and j.isTemporary = :isTemporary")
    Optional<JobPost> findJobPostByIdAndTemporary(@Param("memberId") Long memberId, @Param("jobPostId") Long jobPostId, @Param("isTemporary") Boolean isTemporary);
}
