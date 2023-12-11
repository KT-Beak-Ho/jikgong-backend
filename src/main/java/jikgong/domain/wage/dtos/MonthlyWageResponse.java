package jikgong.domain.wage.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class MonthlyWageResponse {
    private Integer totalMonthlyWage; // 한달 임금 합
    private List<LocalDateTime> workDayList; // 해당 월 일한 날짜
}
