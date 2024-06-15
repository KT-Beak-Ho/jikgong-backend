package jikgong.domain.profit.repository;

import jikgong.domain.profit.entity.Profit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProfitRepository extends JpaRepository<Profit, Long> {

    /**
     * find by id and member
     */
    @Query("select p from Profit p where p.id = :profitId and p.member.id = :memberId")
    Optional<Profit> findByIdAndMember(@Param("memberId") Long memberId, @Param("profitId") Long profitId);


    /**
     * 수익 내역 (캘린더 형식)
     */
    @Query("select p from Profit p where p.member.id = :memberId and p.date = :selectDate")
    List<Profit> findBySelectDate(@Param("memberId") Long memberId, @Param("selectDate") LocalDate selectDate);

    @Query("select sum(p.wage) from Profit p where p.member.id = :memberId and " +
        "month(p.date) = month(:selectMonth) and year(p.date) = year(:selectMonth)")
    Integer findTotalMonthlyWage(@Param("memberId") Long memberId, @Param("selectMonth") LocalDate selectMonth);

    @Query("select SUM(FUNCTION('TIMESTAMPDIFF', MINUTE, p.startTime, p.endTime)) " +
        "from Profit p where p.member.id = :memberId and month(p.date) = month(:selectMonth) and year(p.date) = year(:selectMonth)")
    Integer findWorkTimeInMonth(@Param("memberId") Long memberId, @Param("selectMonth") LocalDate selectMonth);

    @Query("select p from Profit p where p.member.id = :memberId and month(p.date) = month(:selectMonth) and year(p.date) = year(:selectMonth) order by p.date desc")
    List<Profit> findProfitInMonth(@Param("memberId") Long memberId, @Param("selectMonth") LocalDate selectMonth);


    /**
     * 수익 내역 (리스트 형식)
     */
    @Query("select p from Profit p where p.member.id = :memberId order by p.date desc")
    Page<Profit> findByMember(@Param("memberId") Long memberId, Pageable pageable);


    /**
     * 수익 내역 (그래프)
     */
    @Query("select p.date as workDate, SUM(p.wage) as totalWage from Profit p " +
        "where year(p.date) = :year AND month(p.date) = :month group by p.date")
    List<Object[]> getTotalWagePerDay(@Param("year") int year, @Param("month") int month);

    @Query("select month(p.date) as month, sum (p.wage) as totalWage, " +
        "sum(FUNCTION('TIMESTAMPDIFF', MINUTE, p.startTime, p.endTime)) as totalWorkMinutes " +
        "from Profit p where p.member.id = :memberId and year(p.date) = :year group by month(p.date)")
    List<Object[]> getTotalWageAndWorkTimePerMonth(@Param("memberId") Long memberId, @Param("year") int year);


    /**
     * 수익 관리 (메인 페이지)
     */
    @Query("select sum(FUNCTION('TIMESTAMPDIFF', MINUTE, p.startTime, p.endTime)) from Profit p where p.member.id = :memberId")
    Integer findTotalWorkTimeByMember(@Param("memberId") Long memberId);

    @Query("select sum(p.wage) from Profit p where p.member.id = :memberId")
    Integer findTotalWageByMember(@Param("memberId") Long memberId);
}
