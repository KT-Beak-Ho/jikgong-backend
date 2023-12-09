package jikgong.domain.wage.dtos;

import jikgong.domain.wage.entity.Wage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class DailyWageResponse {
    private Integer totalMonthlyWage; // 한달 임금 합
    private List<WageDetailResponse> wageDetailResponseList;
}
