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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

        Wage wage = Wage.createEntity(request, member);

        return wageRepository.save(wage).getId();
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

        // 해당 달의 임금 합
        Integer wageInMonth = wageRepository.findTotalMonthlyWage(
                member.getId(),
                TimeTransfer.getFirstDayOfMonth(selectMonth),
                TimeTransfer.getLastDayOfMonth(selectMonth));

        Integer workTimeInMonth = wageRepository.findWorkTimeInMonth(
                member.getId(),
                TimeTransfer.getFirstDayOfMonth(selectMonth),
                TimeTransfer.getLastDayOfMonth(selectMonth));

        List<Wage> wageHistoryMonth = wageRepository.findWorkDateInMonth(
                member.getId(),
                TimeTransfer.getFirstDayOfMonth(selectMonth),
                TimeTransfer.getLastDayOfMonth(selectMonth));

        List<DailyWageResponse> dailyWageResponseList = wageHistoryMonth.stream()
                .map(DailyWageResponse::from)
                .collect(Collectors.toList());

//        // 일별 일한 시간 합
//        Map<LocalDate, String> dailyWorkTime = wageHistoryMonth.stream()
//                .collect(Collectors.groupingBy(
//                        wage -> wage.getWorkDate(),
//                        Collectors.collectingAndThen(
//                                Collectors.reducing(Duration.ZERO,
//                                        wage -> Duration.between(wage.getStartTime(), wage.getEndTime()),
//                                        Duration::plus),
//                                duration -> formatDuration(duration)
//                        )
//                ));

        // 위와 같은 코드
        /*
        for (Wage wage : wageHistoryMonth) {
            LocalDate date = wage.getStartTime().toLocalDate();
            Duration duration = Duration.between(wage.getStartTime(), wage.getEndTime());

            // 기존에 해당 날짜에 대한 값이 이미 있을 경우 더하고 없으면 새로 추가
            if (dailyWorkTime.containsKey(date)) {
                Duration existingDuration = Duration.parse(dailyWorkTime.get(date));
                dailyWorkTime.put(date, formatDuration(existingDuration.plus(duration)));
            } else {
                dailyWorkTime.put(date, formatDuration(duration));
            }
        } */

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

    // Duration 객체 -> x시 x분 으로 포맷팅
    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();

        return String.format("%d시간 %d분", hours, minutes);
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
}
