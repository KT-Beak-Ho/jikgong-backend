package jikgong.domain.profit.service;

import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.profit.dto.graph.DailyGraphResponse;
import jikgong.domain.profit.dto.graph.MonthlyGraphResponse;
import jikgong.domain.profit.dto.graph.WorkTimeGraphResponse;
import jikgong.domain.profit.dto.history.*;
import jikgong.domain.profit.entity.Profit;
import jikgong.domain.profit.repository.ProfitRepository;
import jikgong.global.exception.JikgongException;
import jikgong.global.exception.ErrorCode;
import jikgong.global.utils.TimeTransfer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfitService {
    private final ProfitRepository profitRepository;
    private final MemberRepository memberRepository;

    /**
     * 수익 내역 저장
     */
    public Long saveProfitHistory(Long workerId, ProfitSaveRequest request) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));
        return profitRepository.save(Profit.createEntity(request, worker)).getId();
    }

    /**
     * 수익 내역 일별 조회 (캘린더 형식)
     */
    @Transactional(readOnly = true)
    public List<DailyProfitResponse> findDailyProfitHistory(Long workerId, LocalDate selectDate) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        // 일일 임금 지급 리스트
        return profitRepository.findBySelectDate(worker.getId(), selectDate).stream()
                .map(DailyProfitResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 수익 내역 월별 조회 (캘린더 형식)
     * 임금 합, 근무 시간 합, 수익 정보 리스트, 출역 날짜 리스트
     */
    @Transactional(readOnly = true)
    public MonthlyProfitResponse findMonthlyProfitHistoryCalendar(Long workerId, LocalDate selectMonth) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        LocalDate monthStart = TimeTransfer.getFirstDayOfMonth(selectMonth);
        LocalDate monthEnd = TimeTransfer.getLastDayOfMonth(selectMonth);

        // 해당 달의 임금 합
        Integer wageInMonth = profitRepository.findTotalMonthlyWage(worker.getId(), selectMonth);

        // 해당 달의 근무 시간 합
        Integer workTimeInMonth = profitRepository.findWorkTimeInMonth(worker.getId(), selectMonth);

        // 한달 간의 수익 데이터
        List<Profit> profitHistoryMonth = profitRepository.findProfitInMonth(worker.getId(), selectMonth);

        List<DailyProfitResponse> dailyProfitResponseList = profitHistoryMonth.stream()
                .map(DailyProfitResponse::from)
                .collect(Collectors.toList());

        // 월별 일한 날짜 리스트
        List<LocalDate> workDateList = profitHistoryMonth.stream()
                .map(Profit::getDate)
                .distinct()
                .collect(Collectors.toList());

        return MonthlyProfitResponse.builder()
                .wageInMonth(wageInMonth)
                .workTimeInMonth(TimeTransfer.getHourMinute(workTimeInMonth))
                .workDateList(workDateList)
                .profitResponseList(dailyProfitResponseList)
                .build();
    }


    /**
     * 수익 내역 삭제
     */
    public void deleteProfitHistory(Long workerId, Long profitId) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        Profit profit = profitRepository.findByIdAndMember(worker.getId(), profitId)
                .orElseThrow(() -> new JikgongException(ErrorCode.PROFIT_NOT_FOUND));

        profitRepository.delete(profit);
    }

    /**
     * 수익 내역 수정
     */
    public void modifyProfitHistory(Long workerId, ProfitModifyRequest request) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        Profit profit = profitRepository.findByIdAndMember(worker.getId(), request.getProfitId())
                .orElseThrow(() -> new JikgongException(ErrorCode.PROFIT_NOT_FOUND));

        // update
        profit.modifyProfit(request);
    }

    /**
     * 수익 내역 (리스트 형식)
     */
    @Transactional(readOnly = true)
    public Page<DailyProfitResponse> findProfitHistoryList(Long workerId, Pageable pageable) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        // 수익 내역 페이징
        Page<Profit> profitPage = profitRepository.findByMember(worker.getId(), pageable);

        List<DailyProfitResponse> profitResponseList = profitPage.getContent().stream()
                .map(DailyProfitResponse::from)
                .collect(Collectors.toList());
        return new PageImpl<>(profitResponseList, pageable, profitPage.getTotalElements());
    }

    /**
     * 수익 관리 메인 페이지
     * 총 수입, 총 근무 시간
     */
    @Transactional(readOnly = true)
    public ProfitSummaryInfoResponse findSummaryInfo(Long workerId) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        Integer totalWorkTime = profitRepository.findTotalWorkTimeByMember(worker.getId());
        Integer totalWage = profitRepository.findTotalWageByMember(worker.getId());

        return ProfitSummaryInfoResponse.builder()
                .totalWorkTime(totalWorkTime)
                .totalWage(totalWage)
                .build();
    }

    /**
     * 수익 일별 그래프 정보
     */
    @Transactional(readOnly = true)
    public DailyGraphResponse findDailyGraphInfo(Long workerId, LocalDate selectMonth) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        // [출역 날짜, 일한 시간] 으로 그룹핑
        List<Object[]> totalWagePerDay = profitRepository.getTotalWagePerDay(selectMonth.getYear(), selectMonth.getMonth().getValue());

        List<Profit> profitInMonth = profitRepository.findProfitInMonth(worker.getId(), selectMonth);

        // key: 날짜  |  value: 일한 시간 합, 시간 정보(시작, 종료)
        // 예시
        // 9시간
        // 06:00 ~ 15:00
        // -------------
        // 12시간
        // 06:00 ~ 15:00
        // 16:00 ~ 17:00
        Map<LocalDate, WorkTimeGraphResponse> totalWorkTimePerDay = new HashMap<>();
        for (Profit profit : profitInMonth) {
            if (totalWorkTimePerDay.containsKey(profit.getDate())) {
                WorkTimeGraphResponse workTimeGraphResponse = totalWorkTimePerDay.get(profit.getDate());
                // 근무 총 시간 더함
                workTimeGraphResponse.plusTime(profit.getStartTime(), profit.getEndTime());
                // 근무 시간 더함
                workTimeGraphResponse.addWorkTime(profit.getStartTime(), profit.getEndTime());

                totalWorkTimePerDay.put(profit.getDate(), workTimeGraphResponse);
            } else {
                totalWorkTimePerDay.put(profit.getDate(), WorkTimeGraphResponse.createDto(profit.getStartTime(), profit.getEndTime()));
            }
        }

        return DailyGraphResponse.builder()
                .totalWagePerDay(totalWagePerDay)
                .totalWorkTimePerDay(totalWorkTimePerDay)
                .build();
    }

    /**
     * 수익 월별 그래프 정보
     */
    @Transactional(readOnly = true)
    public MonthlyGraphResponse findMonthlyGraphInfo(Long workerId, LocalDate selectYear) {
        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        // [월, 일한 시간, 총 수익]
        List<Object[]> totalWageAndWorkTimePerMonth = profitRepository.getTotalWageAndWorkTimePerMonth(worker.getId(), selectYear.getYear());

        return MonthlyGraphResponse
                .builder()
                .totalWageAndWorkTimePerMonth(totalWageAndWorkTimePerMonth)
                .build();
    }
}
