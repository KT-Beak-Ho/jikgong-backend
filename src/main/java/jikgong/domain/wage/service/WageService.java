package jikgong.domain.wage.service;

import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.wage.dtos.graph.DailyGraphResponse;
import jikgong.domain.wage.dtos.graph.MonthlyGraphResponse;
import jikgong.domain.wage.dtos.graph.WorkTimeGraphResponse;
import jikgong.domain.wage.dtos.history.*;
import jikgong.domain.wage.entity.Wage;
import jikgong.domain.wage.repository.WageRepository;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import jikgong.global.utils.TimeTransfer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class WageService {
    private final WageRepository wageRepository;
    private final MemberRepository memberRepository;

    public Long saveWageHistory(Long memberId, WageSaveRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return wageRepository.save(Wage.createEntity(request, member)).getId();
    }

    @Transactional(readOnly = true)
    public List<DailyWageResponse> findDailyWageHistory(Long memberId, LocalDate selectDay) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 일일 임금 지급 리스트
        return wageRepository.findBySelectDay(member.getId(), selectDay).stream()
                .map(DailyWageResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MonthlyWageResponse findMonthlyWageHistoryCalendar(Long memberId, LocalDate selectMonth) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        LocalDate monthStart = TimeTransfer.getFirstDayOfMonth(selectMonth);
        LocalDate monthEnd = TimeTransfer.getLastDayOfMonth(selectMonth);

        // 해당 달의 임금 합
        Integer wageInMonth = wageRepository.findTotalMonthlyWage(member.getId(), monthStart, monthEnd);

        // 해당 달의 근무 시간 합
        Integer workTimeInMonth = wageRepository.findWorkTimeInMonth(member.getId(), monthStart, monthEnd);

        List<Wage> wageHistoryMonth = wageRepository.findWageInMonth(member.getId(), monthStart, monthEnd);

        List<DailyWageResponse> dailyWageResponseList = wageHistoryMonth.stream()
                .map(DailyWageResponse::from)
                .collect(Collectors.toList());

        // 월별 일한 날짜 리스트
        List<LocalDate> workDayList = wageHistoryMonth.stream()
                .map(Wage::getWorkDate)
                .distinct()
                .collect(Collectors.toList());

        return MonthlyWageResponse.builder()
                .wageInMonth(wageInMonth)
                .workTimeInMonth(TimeTransfer.getHourMinute(workTimeInMonth))
                .workDayList(workDayList)
                .wageResponseList(dailyWageResponseList)
                .build();
    }


    public void deleteWageHistory(Long memberId, Long wageId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Wage wage = wageRepository.findByMemberIdAndWageId(member.getId(), wageId);

        wageRepository.delete(wage);
    }

    public void modifyWageHistory(Long memberId, WageModifyRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Wage wage = wageRepository.findById(request.getWageId())
                .orElseThrow(() -> new CustomException(ErrorCode.WAGE_NOT_FOUND));

        // update
        wage.modifyWage(request);
    }

    public List<DailyWageResponse> findWageHistoryList(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return wageRepository.findByMember(member.getId()).stream()
                .map(DailyWageResponse::from)
                .collect(Collectors.toList());
    }

    public WageSummaryInfoResponse findSummaryInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Integer totalWorkTime = wageRepository.findTotalWorkTimeByMember(member.getId());
        Integer totalWage = wageRepository.findTotalWageByMember(member.getId());

        return WageSummaryInfoResponse.builder()
                .totalWorkTime(totalWorkTime)
                .totalWage(totalWage)
                .build();
    }


    public DailyGraphResponse findDailyGraphInfo(Long memberId, LocalDate selectMonth) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<Object[]> totalWagePerDay = wageRepository.getTotalWagePerDay(selectMonth.getYear(), selectMonth.getMonth().getValue());

        List<Wage> wageInMonth = wageRepository.findWageInMonth(
                member.getId(),
                TimeTransfer.getFirstDayOfMonth(selectMonth),
                TimeTransfer.getLastDayOfMonth(selectMonth));

        Map<LocalDate, WorkTimeGraphResponse> totalWorkTimePerDay = new HashMap<>();
        for (Wage wage : wageInMonth) {
            if (totalWorkTimePerDay.containsKey(wage.getWorkDate())) {
                WorkTimeGraphResponse workTimeGraphResponse = totalWorkTimePerDay.get(wage.getWorkDate());

                workTimeGraphResponse.plusTime(wage.getStartTime(), wage.getEndTime());
                workTimeGraphResponse.addWorkTime(wage.getStartTime(), wage.getEndTime());

                totalWorkTimePerDay.put(wage.getWorkDate(), workTimeGraphResponse);
            } else {
                totalWorkTimePerDay.put(wage.getWorkDate(), WorkTimeGraphResponse.createDto(wage.getStartTime(), wage.getEndTime()));
            }
        }

        return DailyGraphResponse.builder()
                .totalWagePerDay(totalWagePerDay)
                .totalWorkTimePerDay(totalWorkTimePerDay)
                .build();
    }

    public MonthlyGraphResponse findMonthlyGraphInfo(Long memberId, LocalDate selectYear) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<Object[]> totalWageAndWorkTimePerMonth = wageRepository.getTotalWageAndWorkTimePerMonth(selectYear.getYear());

        return MonthlyGraphResponse
                .builder()
                .totalWageAndWorkTimePerMonth(totalWageAndWorkTimePerMonth)
                .build();
    }
}
