package jikgong.domain.wage.dtos;

import jikgong.domain.wage.entity.Wage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class DailyWageResponse {
    private Long wageId;

    private Integer dailyWage; // 하루 임금
    private String memo; // 메모
    private String companyName; // 회사 명
    private LocalDateTime startTime; // 근무 시작 시간
    private LocalDateTime endTime; // 근무 종료 시간

    public static DailyWageResponse from(Wage wage) {
        return DailyWageResponse.builder()
                .wageId(wage.getId())
                .dailyWage(wage.getDailyWage())
                .memo(wage.getMemo())
                .companyName(wage.getCompanyName())
                .startTime(wage.getStartTime())
                .endTime(wage.getEndTime())
                .build();
    }
}
