package jikgong.domain.wage.repository;

import jikgong.domain.member.entity.Member;
import jikgong.domain.wage.entity.Wage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WageRepository extends JpaRepository<Wage, Long> {
    @Query("select w from Wage w where w.member.id = :memberId and " +
            "w.startTime between :dayStart and :dayEnd")
    List<Wage> findBySelectDay(@Param("memberId") Long memberId,
                               @Param("dayStart") LocalDateTime dayStart,
                               @Param("dayEnd") LocalDateTime dayEnd);

    @Query("select sum(w.dailyWage) from Wage w where w.member.id = :memberId and " +
            "w.startTime between :monthStart and :monthEnd")
    Integer findTotalMonthlyWage(@Param("memberId") Long memberId,
                              @Param("monthStart") LocalDateTime monthStart,
                              @Param("monthEnd") LocalDateTime monthEnd);

    @Query("select w from Wage w where w.id = :wageId and w.member.id = :memberId")
    Wage findByMemberIdAndWageId(@Param("memberId") Long memberId, @Param("wageId") Long wageId);

    @Query("select w.startTime from Wage w where w.member.id = :memberId and " +
            "w.startTime between :monthStart and :monthEnd")
    List<LocalDateTime> findWorkDateInMonth(@Param("memberId") Long memberId,
                                            @Param("monthStart") LocalDateTime monthStart,
                                            @Param("monthEnd") LocalDateTime monthEnd);
}
