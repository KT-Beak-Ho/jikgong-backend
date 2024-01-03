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
    private Integer totalMonthlyWage; // 한달 임금 합
    private List<LocalDateTime> workDayList; // 해당 월 일한 날짜
    private Map<LocalDate, String> dailyWorkTime; // 날짜별 일한 시간 합
}
