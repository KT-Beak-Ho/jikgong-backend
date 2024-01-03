package jikgong.domain.apply.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ApplyRequest {
    @Schema(description = "jobPostId", example = "1")
    private Long jobPostId;
}
