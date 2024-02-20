package jikgong.domain.wage.service;

import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.wage.dtos.*;
import jikgong.domain.wage.dtos.graph.WageDailyGraphResponse;
import jikgong.domain.wage.dtos.graph.WorkTimeGraphResponse;
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


    public WageDailyGraphResponse findDailyGraphInfo(Long memberId, LocalDate selectMonth) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        LocalDate monthStart = TimeTransfer.getFirstDayOfMonth(selectMonth);
        LocalDate monthEnd = TimeTransfer.getLastDayOfMonth(selectMonth);

        List<Wage> wageInMonth = wageRepository.findWageInMonth(member.getId(), monthStart, monthEnd);

        Map<LocalDate, Integer> wageMap = new HashMap<>();
        Map<LocalDate, WorkTimeGraphResponse> workTimeMap = new HashMap<>();

        for (Wage wage : wageInMonth) {
            if (wageMap.containsKey(wage.getWorkDate())) {
                wageMap.put(wage.getWorkDate(), wageMap.get(wage.getWorkDate()) + wage.getDailyWage());
            } else {
                wageMap.put(wage.getWorkDate(), wage.getDailyWage());
            }
        }

        for (Wage wage : wageInMonth) {
            if (workTimeMap.containsKey(wage.getWorkDate())) {
                WorkTimeGraphResponse workTimeGraphResponse = workTimeMap.get(wage.getWorkDate());

                workTimeGraphResponse.plusTime(wage.getStartTime(), wage.getEndTime());
                workTimeGraphResponse.addWorkTime(wage.getStartTime(), wage.getEndTime());

                workTimeMap.put(wage.getWorkDate(), workTimeGraphResponse);
            } else {
                workTimeMap.put(wage.getWorkDate(), WorkTimeGraphResponse.createDto(wage.getStartTime(), wage.getEndTime()));
            }
        }

        return WageDailyGraphResponse.builder()
                .wageMap(wageMap)
                .workTimeMap(workTimeMap)
                .build();
    }

    public void findMonthlyGraphInfo(Long memberId, LocalDate selectYear) {

    }
}
