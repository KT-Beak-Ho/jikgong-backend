package jikgong.domain.wage.service;

import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.wage.dtos.MonthlyWageResponse;
import jikgong.domain.wage.dtos.DailyWageResponse;
import jikgong.domain.wage.dtos.WageModifyRequest;
import jikgong.domain.wage.dtos.WageSaveRequest;
import jikgong.domain.wage.entity.Wage;
import jikgong.domain.wage.repository.WageRepository;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import jikgong.global.utils.TimeTransfer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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

        Wage wage = Wage.builder()
                .dailyWage(request.getDailyWage())
                .memo(request.getMemo())
                .companyName(request.getCompanyName())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .member(member)
                .build();
        return wageRepository.save(wage).getId();
    }

    @Transactional(readOnly = true)
    public List<DailyWageResponse> findDailyWageHistory(Long memberId, LocalDateTime selectDay) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 일일 임금 지급 리스트
        List<DailyWageResponse> wageDetailResponseList = wageRepository.findBySelectDay(member.getId(),
                        TimeTransfer.getFirstTimeOfDay(selectDay),
                        TimeTransfer.getLastTimeOfDay(selectDay)).stream()
                .map(DailyWageResponse::from)
                .collect(Collectors.toList());

        return wageDetailResponseList;
    }

    @Transactional(readOnly = true)
    public MonthlyWageResponse findMonthlyWageHistory(Long memberId, LocalDateTime selectMonth) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 해당 달의 임금 합
        Integer totalMonthlyWage = wageRepository.findTotalMonthlyWage(
                member.getId(),
                TimeTransfer.getFirstDayOfMonth(selectMonth),
                TimeTransfer.getLastDayOfMonth(selectMonth));

        // 월별 일한 날짜 리스트
        List<LocalDateTime> workDayList = wageRepository.findWorkDateInMonth(
                        member.getId(),
                        TimeTransfer.getFirstDayOfMonth(selectMonth),
                        TimeTransfer.getLastDayOfMonth(selectMonth)).stream()
                .map(TimeTransfer::getFirstTimeOfDay)
                .distinct()
                .collect(Collectors.toList());

        return MonthlyWageResponse.builder()
                .totalMonthlyWage(totalMonthlyWage)
                .workDayList(workDayList)
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
        wage.modifyWage(request.getDailyWage(), request.getMemo(), request.getCompanyName(), request.getStartTime(), request.getEndTime());
    }
}
