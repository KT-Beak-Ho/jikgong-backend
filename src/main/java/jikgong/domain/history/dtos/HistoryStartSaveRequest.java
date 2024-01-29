package jikgong.domain.history.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Getter
@ToString
public class HistoryStartSaveRequest {
    @Schema(description = "출근 인부 id list", example = "[1,2]")
    private List<Long> startWorkMemberIdList;
    @Schema(description = "결근 인부 id list", example = "[3,4]")
    private List<Long> notWorkMemberIdList;
    @Schema(description = "공고 id", example = "1")
    private Long jobPostId;
    @Schema(description = "날짜 id", example = "1")
    private Long workDateId;
}
