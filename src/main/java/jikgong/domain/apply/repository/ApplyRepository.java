package jikgong.domain.apply.repository;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.workDate.entity.WorkDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplyRepository extends JpaRepository<Apply, Long> {
    // fetch join 시 count query 별도 지정 필요
    @Query(value = "select a from Apply a join fetch a.member m where a.workDate.jobPost.member.id = :memberId and a.workDate.jobPost.id = :jobPostId and a.status = :status and a.workDate.workDate = :workDate",
            countQuery = "select a from Apply a where a.workDate.jobPost.member.id = :memberId and a.workDate.jobPost.id = :jobPostId and a.status = :status and a.workDate.workDate = :workDate")
    Page<Apply> findByJobPostIdAndMemberIdAndStatus(@Param("memberId") Long memberId, @Param("jobPostId") Long jobPostId, @Param("status") ApplyStatus status, @Param("workDate") LocalDate workDate, Pageable pageable);

    @Query("select a from Apply a where a.member.id = :memberId and a.workDate.workDate = :workDate and a.status = :applyStatus")
    List<Apply> findByMemberAndWorkDate(@Param("memberId") Long memberId, @Param("workDate") LocalDate workDate, @Param("applyStatus") ApplyStatus applyStatus);

    @Query("select a from Apply a where a.member.id = :memberId and a.workDate.workDate >= :monthStart and a.workDate.workDate <= :monthEnd")
    List<Apply> findByMemberAndWorkMonth(@Param("memberId") Long memberId, @Param("monthStart") LocalDate monthStart, @Param("monthEnd") LocalDate monthEnd);


    @Query("select a from Apply a where a.member.id = :memberId and a.workDate.jobPost.id = :jobPostId")
    Optional<Apply> findByMemberIdAndJobPostId(@Param("memberId") Long memberId, @Param("jobPostId") Long jobPostId);

    @Query("select a from Apply a where a.workDate.jobPost.member.id = :memberId and a.workDate.jobPost.id = :jobPostId and a.member.id = :targetMemberId")
    Optional<Apply> checkAppliedAndAuthor(@Param("memberId") Long memberId, @Param("targetMemberId") Long targetMemberId, @Param("jobPostId") Long jobPostId);

    @Query("select count(a) from Apply a where a.workDate.jobPost.member.id = :memberId and a.workDate.jobPost.id = :jobPostId")
    Long findCountApply(@Param("memberId") Long memberId, @Param("jobPostId") Long jobPostId);

    @Modifying
    @Query("update Apply a set a.status = :applyStatus where a.id in :applyIdList")
    int updateApplyStatus(@Param("applyIdList") List<Long> applyIdList, @Param("applyStatus") ApplyStatus applyStatus);

    @Query("select a from Apply a where a.id in :applyIdList and a.workDate.workDate = :workDate")
    List<Apply> findByIdList(@Param("applyIdList") List<Long> applyIdList, @Param("workDate") LocalDate workDate);

}
