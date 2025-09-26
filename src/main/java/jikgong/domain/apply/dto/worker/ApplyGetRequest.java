package jikgong.domain.apply.dto.worker;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ApplyGetRequest {
    @Schema(description = "노동 시작일자", example = "2024-01-01")
    private LocalDate startWorkDate;
    @Schema(description = "노동 종료일자", example = "2026-12-31")
    private LocalDate endWorkDate;
}
