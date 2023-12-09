package jikgong.domain.wage.service;

import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.wage.dtos.DailyWageResponse;
import jikgong.domain.wage.dtos.WageDetailResponse;
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
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
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
    public DailyWageResponse findDailyWageHistory(Long memberId, LocalDateTime selectDay) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 일일 임금 지급 리스트
        List<WageDetailResponse> wageDetailResponseList = wageRepository.findBySelectDay(member.getId(),
                        TimeTransfer.getFirstTimeOfDay(selectDay),
                        TimeTransfer.getLastTimeOfDay(selectDay)).stream()
                .map(WageDetailResponse::from)
                .collect(Collectors.toList());

        // 해당 달의 임금 합
        Integer totalMonthlyWage = wageRepository.findTotalMonthlyWage(member.getId(),
                TimeTransfer.getFirstDayOfMonth(selectDay),
                TimeTransfer.getLastDayOfMonth(selectDay));

        return DailyWageResponse.builder()
                .totalMonthlyWage(totalMonthlyWage)
                .wageDetailResponseList(wageDetailResponseList)
                .build();
    }
}
