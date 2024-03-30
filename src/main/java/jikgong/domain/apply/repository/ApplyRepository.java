package jikgong.domain.apply.repository;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
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
    /**
     * 인력 관리
     */
    @Query(value = "select a from Apply a join fetch a.member m where a.workDate.jobPost.member.id = :memberId and a.workDate.jobPost.id = :jobPostId and a.status = :status and a.workDate.id = :workDateId",
            countQuery = "select a from Apply a where a.workDate.jobPost.member.id = :memberId and a.workDate.jobPost.id = :jobPostId and a.status = :status and a.workDate.id = :workDateId")
    Page<Apply> findApplyForCompanyByApplyStatus(@Param("memberId") Long memberId, @Param("jobPostId") Long jobPostId, @Param("workDateId") Long workDateId, @Param("status") ApplyStatus status, Pageable pageable);
    @Modifying
    @Query("update Apply a set a.status = :applyStatus, a.statusDecisionTime = :now where a.id in :applyIdList")
    int updateApplyStatus(@Param("applyIdList") List<Long> applyIdList, @Param("applyStatus") ApplyStatus applyStatus, @Param("now") LocalDateTime now);
    @Query("select a from Apply a join fetch a.member m where a.id in :applyIdList and a.workDate.id = :workDateId and a.workDate.jobPost.id = :jobPostId")
    List<Apply> findByIdList(@Param("applyIdList") List<Long> applyIdList, @Param("workDateId") Long workDateId, @Param("jobPostId") Long jobPostId);
    @Query("select a from Apply a where a.workDate.date = :date and a.member.id in :memberIdList and a.id not in :applyIdList")
    List<Apply> deleteOtherApplyByWorkDate(@Param("date") LocalDate date, @Param("applyIdList") List<Long> applyIdList, @Param("memberIdList") List<Long> memberIdList);



    /**
     * 내 일자리
     * 지원 내역
     */
    @Query("select a from Apply a join fetch a.workDate w join fetch a.workDate.jobPost j " +
            "where a.member.id = :memberId and w.date = :date and a.status = :applyStatus")
    List<Apply> findApplyPerDay(@Param("memberId") Long memberId, @Param("date") LocalDate date, @Param("applyStatus") ApplyStatus applyStatus);
    @Query("select a from Apply a join fetch a.workDate w " +
            "where a.member.id = :memberId and w.date between :monthStart and :monthEnd")
    List<Apply> findApplyPerMonth(@Param("memberId") Long memberId, @Param("monthStart") LocalDate monthStart, @Param("monthEnd") LocalDate monthEnd);
    @Query("select a from Apply a join fetch a.workDate w join fetch w.jobPost j where a.member.id = :memberId and a.status = 'PENDING' order by a.createdDate")
    List<Apply> findPendingApply(@Param("memberId") Long memberId);



    /**
     * 일자리 지원 및 취소
     */
    @Query("select a from Apply a where a.member.id = :memberId and a.workDate.date in :workDateList and a.status = 'ACCEPTED'")
    List<Apply> checkAcceptedApplyBeforeSave(@Param("memberId") Long memberId, @Param("workDateList") List<LocalDate> workDateList);
    @Query("select a from Apply a where a.member.id = :memberId and a.workDate.jobPost.id = :jobPostId and a.workDate.id in :workDateList")
    List<Apply> checkDuplication(@Param("memberId") Long memberId, @Param("jobPostId") Long jobPostId, @Param("workDateList") List<Long> workDateList);
    @Query("select a from Apply a join fetch a.workDate.jobPost j where a.member.id = :memberId and a.id = :applyId")
    Optional<Apply> findCancelApply(@Param("memberId") Long memberId, @Param("applyId") Long applyId);



    /**
     * History 로직에서 사용
     */
    @Query("select a from Apply a where a.workDate.jobPost.id = :jobPostId and a.workDate.id = :workDateId and (a.member.id in :startWorkMemberList or a.member.id in :notWorkMemberList) and a.status = :status")
    List<Apply> checkApplyBeforeSaveHistory(@Param("jobPostId") Long jobPostId, @Param("workDateId") Long workDateId, @Param("startWorkMemberList") List<Long> startWorkMemberList, @Param("notWorkMemberList") List<Long> notWorkMemberList, @Param("status") ApplyStatus status);
    @Query(value = "select a from Apply a join fetch a.member m where a.workDate.jobPost.member.id = :memberId and a.workDate.jobPost.id = :jobPostId and a.status = :status and a.workDate.id = :workDateId")
    List<Apply> findApplyAtStartWorkCheck(@Param("memberId") Long memberId, @Param("jobPostId") Long jobPostId, @Param("workDateId") Long workDateId, @Param("status") ApplyStatus status);


    /**
     * 일자리 제안
     */
    @Query("select a from Apply a join fetch a.workDate where a.member.id = :memberId and a.workDate.date between :firstDayOfMonth and :lastDayOfMonth and a.status = 'ACCEPTED'")
    List<Apply> findCantWorkDate(@Param("memberId") Long memberId, @Param("firstDayOfMonth") LocalDate firstDayOfMonth, @Param("lastDayOfMonth") LocalDate lastDayOfMonth);
    @Query("select a from Apply a join fetch a.workDate where a.member.id = :memberId and a.status = 'ACCEPTED'")
    List<Apply> findAllCantWorkDate(@Param("memberId") Long memberId);


    /**
     * 받은 일자리 제안
     */
    @Query("select count(a) from Apply a where a.member.id = :memberId and a.workDate.date = :date and a.status = 'ACCEPTED'")
    int findAcceptedApplyByWorkDate(@Param("memberId") Long memberId, @Param("date") LocalDate date);
}
