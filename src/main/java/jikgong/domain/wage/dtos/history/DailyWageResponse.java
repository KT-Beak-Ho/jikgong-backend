package jikgong.domain.wage.dtos.history;

import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.wage.entity.Wage;
import jikgong.domain.wage.entity.WageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Getter
@Builder
public class DailyWageResponse {
    private Long wageId;

    private String title; // 현장 명
    private LocalDate workDate; // 근무 날짜
    private LocalTime startTime; // 근무 시작 시간
    private LocalTime endTime; // 근무 종료 시간
    private Integer dailyWage; // 하루 임금
    private Tech tech; // 직종
    private WageType wageType; // 입력 타입

    public static DailyWageResponse from(Wage wage) {
        return DailyWageResponse.builder()
                .wageId(wage.getId())
                .title(wage.getTitle())
                .workDate(wage.getWorkDate())
                .startTime(wage.getStartTime())
                .endTime(wage.getEndTime())
                .dailyWage(wage.getDailyWage())
                .tech(wage.getTech())
                .wageType(wage.getWageType())
                .build();
    }
}
