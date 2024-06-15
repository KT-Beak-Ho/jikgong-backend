package jikgong.domain.apply.dto.worker;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ApplySaveRequest {

    @Schema(description = "jobPostId", example = "1")
    private Long jobPostId;
    @Schema(description = "workDate id List", example = "[\"1\", \"2\"]")
    private List<Long> workDateList;
}