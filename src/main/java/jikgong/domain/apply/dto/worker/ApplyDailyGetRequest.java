package jikgong.domain.apply.dto.worker;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class ApplyDailyGetRequest {
    @Schema(description = "조회할 일자", example = "2025-10-25")
    private LocalDate date;
}
