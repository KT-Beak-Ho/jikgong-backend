package jikgong.domain.profit.dto.history;

import java.time.LocalDate;
import java.time.LocalTime;
import jikgong.domain.jobpost.entity.Tech;
import jikgong.domain.profit.entity.Profit;
import jikgong.domain.profit.entity.ProfitType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class DailyProfitResponse {

    private Long profitId;

    private String title; // 현장 명
    private LocalDate date; // 근무 날짜
    private LocalTime startTime; // 근무 시작 시간
    private LocalTime endTime; // 근무 종료 시간
    private Integer wage; // 하루 임금
    private Tech tech; // 직종
    private ProfitType profitType; // 입력 타입

    public static DailyProfitResponse from(Profit profit) {
        return DailyProfitResponse.builder()
            .profitId(profit.getId())
            .title(profit.getTitle())
            .date(profit.getDate())
            .startTime(profit.getStartTime())
            .endTime(profit.getEndTime())
            .wage(profit.getWage())
            .tech(profit.getTech())
            .profitType(profit.getProfitType())
            .build();
    }
}
