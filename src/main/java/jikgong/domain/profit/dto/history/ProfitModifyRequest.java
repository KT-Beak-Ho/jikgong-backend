package jikgong.domain.profit.dto.history;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import jikgong.domain.profit.entity.ProfitType;
import jikgong.domain.workexperience.entity.Tech;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class ProfitModifyRequest {

    @Schema(description = "profitId", example = "1")
    private Long profitId;
    @Schema(description = "일급", example = "130000")
    private Integer wage; // 일급
    @Schema(description = "현장명", example = "사하구 낙동5블럭 낙동강 온도 측정 센서")
    private String title; // 현장명
    @Schema(description = "근무 날짜", example = "2024-01-01", type = "string")
    private LocalDate date; // 근무 날짜
    @Schema(description = "근무 시작 시간", example = "09:30:00", type = "string")
    private LocalTime startTime; // 근무 시작 시간
    @Schema(description = "근무 종료 시간", example = "18:00:00", type = "string")
    private LocalTime endTime; // 근무 종료 시간
    @Schema(description = "직종", example = "NORMAL")
    private Tech tech; // 직종
    @Schema(description = "입력 타입", example = "CUSTOM")
    private ProfitType profitType; // 입력 타입
}
