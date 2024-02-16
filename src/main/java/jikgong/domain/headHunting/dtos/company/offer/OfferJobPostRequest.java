package jikgong.domain.headHunting.dtos.company.offer;

import io.swagger.v3.oas.annotations.media.Schema;
import jikgong.domain.workDate.entity.WorkDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
public class OfferJobPostRequest {
    @Schema(description = "jobPostId", example = "1")
    private Long jobPostId;
    @Schema(description = "제안 날짜 목록", example = "[\"2024-02-01\", \"2024-02-02\"]")
    private List<LocalDate> workDateList;
}
