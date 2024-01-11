package jikgong.domain.history.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
public class HistorySaveRequest {
    @Schema(description = "인부 id", example = "1")
    private Long targetMemberId;
    @Schema(description = "공고 id", example = "1")
    private Long jobPostId;
    @Schema(description = "출근 or 결근", example = "true")
    private Boolean isWork;
    @Schema(description = "일한 날짜", example = "2024-01-01")
    private LocalDate workDate;
}
