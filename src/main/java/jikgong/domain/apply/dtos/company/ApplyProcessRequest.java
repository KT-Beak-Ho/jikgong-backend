package jikgong.domain.apply.dtos.company;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@ToString
public class ApplyProcessRequest {
    @Schema(description = "jobPostId", example = "1")
    private Long jobPostId;
    @Schema(description = "수락할 요청 Id 리스트", example = "[\"1\", \"2\"]")
    private List<Long> applyIdList;
    @Schema(description = "수락(true) or 거절(false)", example = "true")
    private Boolean isAccept;
    @Schema(description = "선택 날짜", example = "2024-01-01")
    private LocalDate workDate;
}
