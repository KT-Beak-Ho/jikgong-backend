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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplyRepository extends JpaRepository<Apply, Long> {
    // fetch join 시 count query 별도 지정 필요
    @Query(value = "select a from Apply a join fetch a.member m where a.workDate.jobPost.member.id = :memberId and a.workDate.jobPost.id = :jobPostId and a.status = :status and a.workDate.id = :workDateId",
            countQuery = "select a from Apply a where a.workDate.jobPost.member.id = :memberId and a.workDate.jobPost.id = :jobPostId and a.status = :status and a.workDate.id = :workDateId")
    Page<Apply> findByMemberAndJobPostAndWorkDate(@Param("memberId") Long memberId, @Param("jobPostId") Long jobPostId, @Param("workDateId") Long workDateId, @Param("status") ApplyStatus status, Pageable pageable);


    @Query("select a from Apply a join fetch a.workDate w join fetch a.workDate.jobPost j " +
            "where a.member.id = :memberId and w.workDate = :workDate and a.status = :applyStatus")
    List<Apply> findApplyPerDay(@Param("memberId") Long memberId, @Param("workDate") LocalDate workDate, @Param("applyStatus") ApplyStatus applyStatus);

    @Query("select a from Apply a join fetch a.workDate w " +
            "where a.member.id = :memberId and w.workDate between :monthStart and :monthEnd")
    List<Apply> findApplyPerMonth(@Param("memberId") Long memberId, @Param("monthStart") LocalDate monthStart, @Param("monthEnd") LocalDate monthEnd);


    @Query("select a from Apply a where a.member.id = :memberId and a.workDate.jobPost.id = :jobPostId and a.workDate.id in :workDateList")
    List<Apply> checkDuplication(@Param("memberId") Long memberId, @Param("jobPostId") Long jobPostId, @Param("workDateList") List<Long> workDateList);

    @Modifying
    @Query("update Apply a set a.status = :applyStatus, a.statusDecisionTime = :now where a.id in :applyIdList")
    int updateApplyStatus(@Param("applyIdList") List<Long> applyIdList, @Param("applyStatus") ApplyStatus applyStatus, @Param("now") LocalDateTime now);

    @Query("select a from Apply a join fetch a.member m where a.id in :applyIdList and a.workDate.id = :workDateId and a.workDate.jobPost.id = :jobPostId")
    List<Apply> findByIdList(@Param("applyIdList") List<Long> applyIdList, @Param("workDateId") Long workDateId, @Param("jobPostId") Long jobPostId);

    @Query("select a from Apply a where a.workDate.workDate = :workDate and a.member.id in :memberIdList and a.id not in :applyIdList")
    List<Apply> deleteOtherApplyByWorkDate(@Param("workDate") LocalDate workDate, @Param("applyIdList") List<Long> applyIdList, @Param("memberIdList") List<Long> memberIdList);

    @Query("select a from Apply a where a.member.id = :memberId and a.workDate.workDate in :workDateList and a.status = 'ACCEPTED'")
    List<Apply> checkAcceptedApply(@Param("memberId") Long memberId, @Param("workDateList") List<LocalDate> workDateList);

    @Query(value = "select a from Apply a join fetch a.workDate w join fetch w.jobPost j where a.member.id = :memberId and a.status = 'PENDING'",
            countQuery = "select a from Apply a where a.member.id = :memberId and a.status = 'PENDING'")
    Page<Apply> findPendingApply(@Param("memberId") Long memberId, Pageable pageable);

    @Query("select a from Apply a where a.workDate.jobPost.id = :jobPostId and a.workDate.id = :workDateId and (a.member.id in :startWorkMemberList or a.member.id in :notWorkMemberList) and a.status = :status")
    List<Apply> checkApplyBeforeSaveHistory(@Param("jobPostId") Long jobPostId, @Param("workDateId") Long workDateId, @Param("startWorkMemberList") List<Long> startWorkMemberList, @Param("notWorkMemberList") List<Long> notWorkMemberList, @Param("status") ApplyStatus status);

    @Query(value = "select a from Apply a join fetch a.member m where a.workDate.jobPost.member.id = :memberId and a.workDate.jobPost.id = :jobPostId and a.status = :status and a.workDate.id = :workDateId")
    List<Apply> findApplyAtStartWorkCheck(@Param("memberId") Long memberId, @Param("jobPostId") Long jobPostId, @Param("workDateId") Long workDateId, @Param("status") ApplyStatus status);

    @Query("select a from Apply a join fetch a.workDate where a.member.id = :memberId and a.workDate.workDate between :firstDayOfMonth and :lastDayOfMonth and a.status = 'ACCEPTED'")
    List<Apply> findCantWorkDate(@Param("memberId") Long memberId, @Param("firstDayOfMonth") LocalDate firstDayOfMonth, @Param("lastDayOfMonth") LocalDate lastDayOfMonth);

    @Query("select a from Apply a join fetch a.workDate where a.member.id = :memberId and a.status = 'ACCEPTED'")
    List<Apply> findAllCantWorkDate(@Param("memberId") Long memberId);

    // spring batch
    @Query(value = "select a from Apply a join fetch a.workDate w join fetch a.member m join fetch a.workDate.jobPost j where w.workDate = :tomorrow",
    countQuery = "select count(a) from Apply a join a.workDate w where w.workDate = :tomorrow")
    Page<WorkDate> findNeedToCancel(@Param("tomorrow") LocalDate tomorrow, Pageable pageable);
}
