package jikgong.domain.wage.repository;

import jikgong.domain.member.entity.Member;
import jikgong.domain.wage.entity.Wage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@Repository
public interface WageRepository extends JpaRepository<Wage, Long> {
    @Query("select w from Wage w where w.member.id = :memberId and w.workDate = :selectDay")
    List<Wage> findBySelectDay(@Param("memberId") Long memberId, @Param("selectDay") LocalDate selectDay);

    @Query("select sum(w.dailyWage) from Wage w where w.member.id = :memberId and " +
            "w.workDate between :monthStart and :monthEnd")
    Integer findTotalMonthlyWage(@Param("memberId") Long memberId, @Param("monthStart") LocalDate monthStart, @Param("monthEnd") LocalDate monthEnd);

    @Query("select w from Wage w where w.id = :wageId and w.member.id = :memberId")
    Wage findByMemberIdAndWageId(@Param("memberId") Long memberId, @Param("wageId") Long wageId);

    @Query("select w from Wage w where w.member.id = :memberId and w.workDate between :monthStart and :monthEnd order by w.workDate desc")
    List<Wage> findWageInMonth(@Param("memberId") Long memberId, @Param("monthStart") LocalDate monthStart, @Param("monthEnd") LocalDate monthEnd);

    @Query("select w.workDate as month, sum (w.dailyWage) as totalWage, " +
            "sum(FUNCTION('TIMESTAMPDIFF', MINUTE, w.startTime, w.endTime)) as totalWorkMinutes " +
            "from Wage w where year(w.workDate) = :year group by month(w.workDate)")
    List<Object[]> getTotalWageAndWorkTimePerMonth(@Param("year") int year);

    @Query("select w.workDate as workDate, SUM(w.dailyWage) as totalWage from Wage w " +
            "where year(w.workDate) = :year AND month(w.workDate) = :month group by w.workDate")
    List<Object[]> getTotalWagePerDay(@Param("year") int year, @Param("month") int month);

    @Query("SELECT SUM(FUNCTION('TIMESTAMPDIFF', MINUTE, w.startTime, w.endTime)) " +
            "FROM Wage w " +
            "WHERE w.member.id = :memberId AND w.workDate BETWEEN :monthStart AND :monthEnd")
    Integer findWorkTimeInMonth(@Param("memberId") Long memberId, @Param("monthStart") LocalDate monthStart, @Param("monthEnd") LocalDate monthEnd);


    @Query("select w from Wage w where w.member.id = :memberId order by w.workDate asc")
    List<Wage> findByMember(@Param("memberId") Long memberId);

    @Query("select sum(FUNCTION('TIMESTAMPDIFF', MINUTE, w.startTime, w.endTime)) from Wage w where w.member.id = :memberId")
    Integer findTotalWorkTimeByMember(@Param("memberId") Long memberId);

    @Query("select sum(w.dailyWage) from Wage w where w.member.id = :memberId")
    Integer findTotalWageByMember(@Param("memberId") Long memberId);
}
