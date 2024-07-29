package jikgong.domain.apply.repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplyRepository extends JpaRepository<Apply, Long> {

    /**
     * 인력 관리
     */
    @Query(value = "select a from Apply a join fetch a.member m where a.workDate.jobPost.member.id = :memberId and a.workDate.jobPost.id = :jobPostId and a.status = :status and a.workDate.id = :workDateId",
        countQuery = "select a from Apply a where a.workDate.jobPost.member.id = :memberId and a.workDate.jobPost.id = :jobPostId and a.status = :status and a.workDate.id = :workDateId")
    Page<Apply> findApplyForCompanyByApplyStatus(@Param("memberId") Long memberId, @Param("jobPostId") Long jobPostId,
        @Param("workDateId") Long workDateId, @Param("status") ApplyStatus status, Pageable pageable);

    @Query("select a from Apply a join fetch a.member m where a.id in :applyIdList and a.workDate.id = :workDateId and a.workDate.jobPost.id = :jobPostId")
    List<Apply> findByIdList(@Param("applyIdList") List<Long> applyIdList, @Param("workDateId") Long workDateId,
        @Param("jobPostId") Long jobPostId);

    @Query("select a.id from Apply a where a.workDate.date = :date and a.member.id in :memberIdList and a.status = 'PENDING'")
    List<Long> deleteOtherApplyOnDate(@Param("memberIdList") List<Long> memberIdList, @Param("date") LocalDate date);

    @Query("select a from Apply a join fetch a.member m where a.member.id in :memberIdList and a.workDate.date = :date and a.status = 'ACCEPTED'")
    List<Apply> findAcceptedMember(@Param("memberIdList") List<Long> memberIdList, @Param("date") LocalDate date);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Apply a where a.member.id in :memberIdList and a.workDate.date = :date")
    void findByMemberForLock(@Param("memberIdList") List<Long> memberIdList, @Param("date") LocalDate date);


    /**
     * 확정 시 다른 지원 취소
     */
    @Modifying
    @Query("update Apply a set a.status = :applyStatus, a.statusDecisionTime = :now where a.id in :applyIdList")
    int updateApplyStatus(@Param("applyIdList") List<Long> applyIdList, @Param("applyStatus") ApplyStatus applyStatus,
        @Param("now") LocalDateTime now);


    /**
     * 내 일자리, 지원 내역
     */
    @Query("select a from Apply a join fetch a.workDate w join fetch a.workDate.jobPost j " +
        "where a.member.id = :memberId and w.date = :date and a.status = :applyStatus")
    Optional<Apply> findApplyPerDay(@Param("memberId") Long memberId, @Param("date") LocalDate date,
        @Param("applyStatus") ApplyStatus applyStatus);

    @Query("select a from Apply a join fetch a.workDate w " +
        "where a.member.id = :memberId and w.date between :monthStart and :monthEnd")
    List<Apply> findApplyPerMonth(@Param("memberId") Long memberId, @Param("monthStart") LocalDate monthStart,
        @Param("monthEnd") LocalDate monthEnd);

    @Query("select a from Apply a join fetch a.workDate w join fetch w.jobPost j where a.member.id = :memberId and a.status = 'PENDING' order by a.createdDate desc")
    List<Apply> findPendingApply(@Param("memberId") Long memberId);

    @Query("select a from Apply a join fetch a.workDate w join fetch w.jobPost j where a.member.id = :memberId and w.date >= :now order by a.createdDate")
    List<Apply> findFutureApply(@Param("memberId") Long memberId, @Param("now") LocalDate now);


    /**
     * 일자리 지원 및 취소
     */
    @Query("select a from Apply a where a.member.id = :memberId and a.workDate.date in :workDateList and a.status = 'ACCEPTED'")
    List<Apply> checkAcceptedApplyForApply(@Param("memberId") Long memberId,
        @Param("workDateList") List<LocalDate> workDateList);

    @Query("select a from Apply a where a.member.id = :memberId and a.workDate.jobPost.id = :jobPostId and a.workDate.id in :workDateList")
    List<Apply> checkDuplication(@Param("memberId") Long memberId, @Param("jobPostId") Long jobPostId,
        @Param("workDateList") List<Long> workDateList);

    @Query("select a from Apply a join fetch a.workDate.jobPost j where a.member.id = :memberId and a.id = :applyId")
    Optional<Apply> findCancelApply(@Param("memberId") Long memberId, @Param("applyId") Long applyId);


    /**
     * History 로직에서 사용
     */
    @Query("select a from Apply a where a.workDate.id = :workDateId and (a.member.id in :startWorkMemberList or a.member.id in :notWorkMemberList) and a.status = :status")
    List<Apply> checkApplyBeforeSaveHistory(@Param("workDateId") Long workDateId,
        @Param("startWorkMemberList") List<Long> startWorkMemberList,
        @Param("notWorkMemberList") List<Long> notWorkMemberList, @Param("status") ApplyStatus status);

    @Query(value = "select a from Apply a join fetch a.member m where a.status = :status and a.workDate.id = :workDateId")
    List<Apply> findApplyBeforeHistoryProcess(@Param("workDateId") Long workDateId,
        @Param("status") ApplyStatus status);


    /**
     * 일자리 제안
     */
    @Query("select a from Apply a join fetch a.workDate where a.member.id = :memberId and a.workDate.date between :firstDayOfMonth and :lastDayOfMonth and a.status = 'ACCEPTED'")
    List<Apply> findCantWorkDate(@Param("memberId") Long memberId, @Param("firstDayOfMonth") LocalDate firstDayOfMonth,
        @Param("lastDayOfMonth") LocalDate lastDayOfMonth);

    @Query("select a from Apply a join fetch a.workDate where a.member.id = :memberId and a.status = 'ACCEPTED'")
    List<Apply> findAllCantWorkDate(@Param("memberId") Long memberId);


    /**
     * 받은 일자리 제안
     */
    @Query("select count(a) from Apply a where a.member.id = :memberId and a.workDate.date = :date and a.status = 'ACCEPTED'")
    int findAcceptedApplyByWorkDate(@Param("memberId") Long memberId, @Param("date") LocalDate date);

    @Query("select a from Apply a where a.member.id = :memberId and a.workDate.date = :date and a.status = 'ACCEPTED'")
    List<Apply> checkAcceptedApplyForOffer(@Param("memberId") Long memberId, @Param("date") LocalDate date);

    @Query("select a.id from Apply a where a.workDate.date = :date and a.member.id = :memberId and a.status = 'PENDING'")
    List<Long> deleteOtherApplyOnDate(@Param("memberId") Long memberId, @Param("date") LocalDate date);

    @Query("select a from Apply a where a.member.id = :memberId and a.workDate.id = :workDateId and a.status = 'OFFERED'")
    Optional<Apply> findOfferedApply(@Param("memberId") Long memberId, @Param("workDateId") Long workDateId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Apply a where a.member.id = :memberId and a.workDate.date = :date")
    List<Apply> findByMemberForLock(@Param("memberId") Long memberId, @Param("date") LocalDate date);
//
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("select a from Apply a where a.member.id in :memberIdList")
//    List<Apply> findWorkerApplyListForLock(@Param("memberIdList") List<Long> memberIdList);
}
