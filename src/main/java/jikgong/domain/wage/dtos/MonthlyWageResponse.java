package jikgong.domain.wage.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Builder
public class MonthlyWageResponse {
    private Integer wageInMonth; // 한달 임금 합
    private String workTimeInMonth; // 한달 근무 시간 합
    private List<LocalDate> workDayList; // 해당 월 일한 날짜
    private List<DailyWageResponse> wageResponseList; // 수익 내역 리스트
}
