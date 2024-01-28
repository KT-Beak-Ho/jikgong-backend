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
    @Query("select h from History h join fetch Member m where h.jobPost.id = :jobPostId and h.workDate = :workDate and h.isWork = :isWork")
    List<History> findHistoryByJobPostIdAndWork(@Param("jobPostId") Long jobPostId, @Param("workDate") LocalDate workDate, @Param("isWork") Boolean isWork);

    @Query("select h from History h join fetch Member m where h.jobPost.id = :jobPostId and h.jobPost.member.id = :memberId and h.workDate = :workDate and h.isWork = :isWork")
    Page<History> findWorkHistory(@Param("memberId") Long memberId, @Param("jobPostId") Long jobPostId, @Param("workDate") LocalDate workDate, @Param("isWork") Boolean isWork, Pageable pageable);

    @Query("select count(h) from History h where h.jobPost.id = :jobPostId and h.jobPost.member.id = :memberId and h.workDate = :workDate and h.isWork = :isWork")
    Long findCountWorkOrNotWork(@Param("memberId") Long memberId, @Param("jobPostId") Long jobPostId, @Param("workDate") LocalDate workDate, @Param("isWork") Boolean isWork);

    @Modifying
    @Query("delete from History h where h.workDate = :workDateId and (h.member.id in :startWorkList or h.member.id in :notWorkList)")
    int deleteByWorkDateAndAndMember(@Param("startWorkList") List<Long> startWorkList,
                                             @Param("notWorkList") List<Long> notWorkList,
                                             @Param("workDateId") Long workDateId);

    @Modifying
    @Query("update History h set h.status = :status where h.id in :finishWorkList")
    void updateHistoryByIdList(@Param("finishWorkList") List<Long> finishWorkList, @Param("workStatus") WorkStatus status);
}
