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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    // 기존 histroy 데이터 제거
    @Modifying
    @Query("delete from History h where h.workDate.id = :workDateId and (h.member.id in :startWorkList or h.member.id in :notWorkList)")
    int deleteByWorkDateAndAndMember(@Param("startWorkList") List<Long> startWorkList,
                                             @Param("notWorkList") List<Long> notWorkList,
                                             @Param("workDateId") Long workDateId);

    // 퇴근, 조퇴 결과 갱신
    @Modifying
    @Query("update History h set h.endStatus = :status, h.endStatusDecisionTime = :now where h.workDate.id = :workDateId and h.id in :workerIdList and h.startStatus != 'NOT_WORK'")
    int updateHistoryByIdList(@Param("workDateId") Long workDateId, @Param("workerIdList") List<Long> workerIdList, @Param("status") WorkStatus status, @Param("now") LocalDateTime now);

    // 출근, 결근 조회
    @Query("select h from History h join fetch h.member m where h.workDate.id = :workDateId and h.startStatus = :status")
    List<History> findHistoryBeforeProcess(@Param("workDateId") Long workDateId, @Param("status") WorkStatus status);

    // 지급 내역서 조회
    @Query("select h from History h join fetch h.member m join fetch h.workDate.jobPost j " +
            "where h.workDate.id = :workDateId")
    List<History> findPaymentStatementInfo(@Param("workDateId") Long workDateId);
}
