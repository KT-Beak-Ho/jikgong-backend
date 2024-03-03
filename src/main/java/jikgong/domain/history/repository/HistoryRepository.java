package jikgong.domain.history.repository;

import jikgong.domain.history.entity.History;
import jikgong.domain.history.entity.WorkStatus;
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
public interface HistoryRepository extends JpaRepository<History, Long> {
    @Modifying
    @Query("delete from History h where h.workDate.id = :workDateId and (h.member.id in :startWorkList or h.member.id in :notWorkList)")
    int deleteByWorkDateAndAndMember(@Param("startWorkList") List<Long> startWorkList,
                                             @Param("notWorkList") List<Long> notWorkList,
                                             @Param("workDateId") Long workDateId);

    @Modifying
    @Query("update History h set h.endStatus = :status where h.id in :finishWorkList and h.startStatus != 'NOT_WORK'")
    int updateHistoryByIdList(@Param("finishWorkList") List<Long> finishWorkList, @Param("status") WorkStatus status);

    @Query("select h from History h join fetch h.member m where h.workDate.jobPost.id = :jobPostId and h.workDate.id = :workDateId and h.startStatus = :status")
    List<History> findHistoryAtStartWorkCheck(@Param("jobPostId") Long jobPostId, @Param("workDateId") Long workDateId, @Param("status") WorkStatus status);

    @Query("select h from History h join fetch h.member m where h.workDate.jobPost.id = :jobPostId and h.workDate.id = :workDateId and h.startStatus = :status")
    List<History> findHistoryAtFinishWorkCheck(@Param("jobPostId") Long jobPostId, @Param("workDateId") Long workDateId, @Param("status") WorkStatus status);

    @Query("select h from History h join fetch h.member m join fetch h.workDate.jobPost j " +
            "where j.id = :jobPostId and h.workDate.id = :workDateId")
    List<History> findPaymentStatementInfo(@Param("jobPostId") Long jobPostId, @Param("workDateId") Long workDateId);
}
