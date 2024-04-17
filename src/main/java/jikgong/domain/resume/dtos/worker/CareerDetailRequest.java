package jikgong.domain.resume.dtos.worker;

import io.swagger.v3.oas.annotations.media.Schema;
import jikgong.domain.jobPost.entity.Tech;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class CareerDetailRequest {
    @Schema(description = "인부 타입", example = "NORMAL")
    private Tech tech;
    @Schema(description = "기간 (1년 2개월 이라면 14", example = "14")
    private Integer skillPeriod;
}
