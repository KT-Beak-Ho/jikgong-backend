package jikgong.domain.history.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jikgong.domain.history.entity.WorkStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class HistoryPutRequest {
    @Schema(description = "근무 시작 상태: START_WORK(출근)/ NOT_WORK(결근)", example = "START_WORK")
    private WorkStatus startStatus;
    @Schema(description = "근무 시작 시간", example = "2025-09-20T09:00:00")
    private LocalDateTime startStatusDecisionTime;
    @Schema(description = "근무 종료 상태: FINISH_WORK(퇴근)/ EARLY_LEAVE(조퇴)", example = "FINISH_WORK")
    private WorkStatus endStatus;
    @Schema(description = "근무 종료 시간", example = "2025-09-20T18:00:00")
    private LocalDateTime endStatusDecisionTime;
    @Schema(description = "근무 기록 메모", example = "근무 중 화상 입음. 의료 지원 필요함.")
    private String description;
}
