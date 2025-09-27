package jikgong.domain.jobpost.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import jikgong.domain.jobpost.entity.jobpost.JobPost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, Long>, JobPostQuerydslRepository {

    /**
     * find by id and member
     */
    @Query("select j from JobPost j where j.member.id = :memberId and j.id = :jobPostId and j.isTemporary = false")
    Optional<JobPost> findByIdAndMember(@Param("memberId") Long memberId, @Param("jobPostId") Long jobPostId);


    /**
     * 프로젝트 별 공고 조회
     */
    @Query("select j from JobPost j where j.endDate < :now and j.isTemporary = false and j.project.id = :projectId")
    List<JobPost> findCompletedJobPostByMemberAndProject(@Param("now") LocalDate now,
        @Param("projectId") Long projectId, Pageable pageable);

    @Query("select j from JobPost j where j.startDate < :now and j.endDate > :now and j.isTemporary = false and j.project.id = :projectId")
    List<JobPost> findInProgressJobPostByMemberAndProject(@Param("now") LocalDate now,
        @Param("projectId") Long projectId, Pageable pageable);

    @Query("select j from JobPost j where j.startDate > :now and j.isTemporary = false and j.project.id = :projectId")
    List<JobPost> findPlannedJobPostByMemberAndProject(@Param("now") LocalDate now, @Param("projectId") Long projectId,
        Pageable pageable);

    @Query("select j from JobPost j where j.project.id = :projectId")
    List<JobPost> findJobPostByMember(@Param("projectId") Long projectId, Pageable pageable);


    /**
     * 임시 저장
     */
    @Query("select j from JobPost j where j.member.id = :memberId and j.isTemporary = true order by j.createdDate")
    List<JobPost> findTemporaryJobPostByMemberId(@Param("memberId") Long memberId);

    @Query("select j from JobPost j where j.member.id = :memberId and j.id = :jobPostId and j.isTemporary = :isTemporary")
    Optional<JobPost> findTemporaryForDelete(@Param("memberId") Long memberId, @Param("jobPostId") Long jobPostId,
        @Param("isTemporary") Boolean isTemporary);


    /**
     * 일자리 지원
     */
    @Query("select j from JobPost j where j.id = :jobPostId and j.isTemporary = :isTemporary")
    Optional<JobPost> findNotTemporaryJobPost(@Param("jobPostId") Long jobPostId,
        @Param("isTemporary") Boolean isTemporary);


    /**
     * 일자리 제안
     */
    @Query("select j from JobPost j where j.project.id = :projectId")
    List<JobPost> findByProject(@Param("projectId") Long projectId);

    @Query("select j from JobPost j where j.id in :jobPostIdList")
    List<JobPost> findByIdList(@Param("jobPostIdList") List<Long> jobPostIdList);


    /**
     * 노동자: 공고 조회
     */
    @Query("select j from JobPost j join fetch j.member m where j.id = :jobPostId")
    Optional<JobPost> findByIdWithMember(@Param("jobPostId") Long jobPostId);

    /**
     * 프로젝트 삭제 전 검사
     */
    @Query("select count(j) > 0 from JobPost j where j.project.id = :projectId")
    boolean existsNotDeletedJobPost(@Param("projectId") Long projectId);

}
