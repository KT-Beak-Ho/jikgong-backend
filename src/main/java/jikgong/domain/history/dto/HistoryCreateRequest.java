package jikgong.domain.history.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class HistoryCreateRequest {
    @Schema(description = "인부 id", example = "2")
    private Long workerId;
    @Schema(description = "공고 id", example = "1")
    private Long jobPostId;
    @Schema(description = "날짜 id", example = "1")
    private Long workDateId;
}
