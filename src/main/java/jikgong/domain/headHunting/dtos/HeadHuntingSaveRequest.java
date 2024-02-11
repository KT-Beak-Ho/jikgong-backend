package jikgong.domain.headHunting.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jikgong.domain.jobPost.entity.Park;
import jikgong.domain.jobPost.entity.Tech;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class HeadHuntingSaveRequest {
    private Integer career;

    @Schema(description = "가능 직종 리스트", example = "[\"NORMAL\"]")
    private List<Tech> techList;

    // 가능 여부 정보
    @Schema(description = "식사 제공 여부", example = "true")
    private Boolean meal;
    @Schema(description = "픽업 여부", example = "true")
    private Boolean pickup;
    @Schema(description = "주차 가능 여부", example = "FREE")
    private Park park;
}
