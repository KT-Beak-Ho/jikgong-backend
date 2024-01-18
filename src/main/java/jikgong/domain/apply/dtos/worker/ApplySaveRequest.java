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
    @Schema(description = "선택 날짜", example = "[\"2024-01-01\", \"2024-01-02\"]")
    private List<LocalDate> workDateList;
}

