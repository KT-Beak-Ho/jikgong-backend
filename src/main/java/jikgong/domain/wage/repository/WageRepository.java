package jikgong.domain.wage.repository;

import jikgong.domain.member.entity.Member;
import jikgong.domain.wage.entity.Wage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WageRepository extends JpaRepository<Wage, Long> {
    @Query("select w from Wage w where w.member.id = :memberId and w.workDate = :selectDay")
    List<Wage> findBySelectDay(@Param("memberId") Long memberId, @Param("selectDay") LocalDate selectDay);

    @Query("select sum(w.dailyWage) from Wage w where w.member.id = :memberId and " +
            "w.workDate between :monthStart and :monthEnd")
    Integer findTotalMonthlyWage(@Param("memberId") Long memberId,
                                 @Param("monthStart") LocalDate monthStart,
                                 @Param("monthEnd") LocalDate monthEnd);

    @Query("select w from Wage w where w.id = :wageId and w.member.id = :memberId")
    Wage findByMemberIdAndWageId(@Param("memberId") Long memberId, @Param("wageId") Long wageId);

    @Query("select w from Wage w where w.member.id = :memberId and " +
            "w.workDate between :monthStart and :monthEnd order by w.startTime asc")
    List<Wage> findWorkDateInMonth(@Param("memberId") Long memberId,
                                   @Param("monthStart") LocalDate monthStart,
                                   @Param("monthEnd") LocalDate monthEnd);

    @Query("SELECT SUM(FUNCTION('TIMESTAMPDIFF', MINUTE, w.startTime, w.endTime)) " +
            "FROM Wage w " +
            "WHERE w.member.id = :memberId " +
            "AND w.workDate BETWEEN :monthStart AND :monthEnd")
    Integer findWorkTimeInMonth(
            @Param("memberId") Long memberId,
            @Param("monthStart") LocalDate monthStart,
            @Param("monthEnd") LocalDate monthEnd
    );


}
