package jikgong.domain.apply.dtos.worker;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
public class ApplySaveRequest {
    @Schema(description = "jobPostId", example = "1")
    private Long jobPostId;
    @Schema(description = "workDate id List", example = "[\"1\", \"2\"]")
    private List<Long> workDateList;
}

