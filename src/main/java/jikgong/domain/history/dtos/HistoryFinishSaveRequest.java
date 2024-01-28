package jikgong.domain.history.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Getter
@ToString
public class HistoryFinishSaveRequest {
    @Schema(description = "퇴근 history id list", example = "[\"1\", \"2\"]")
    private List<Long> finishWorkList;
    @Schema(description = "조퇴 history id list", example = "[\"3\", \"4\"]")
    private List<Long> earlyLeaveList;
    @Schema(description = "공고 id", example = "1")
    private Long jobPostId;
    @Schema(description = "날짜 id", example = "1")
    private Long workDateId;
}
