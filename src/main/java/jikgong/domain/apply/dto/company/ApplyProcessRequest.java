package jikgong.domain.apply.dto.company;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    @Schema(description = "workDate Id", example = "1")
    private Long workDateId;
}
