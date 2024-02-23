package jikgong.domain.resume.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jikgong.domain.jobPost.entity.Park;
import jikgong.domain.jobPost.entity.Tech;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalTime;
import java.util.List;

@NoArgsConstructor
@Getter
@ToString
public class ResumeSaveRequest {
    @Schema(description = "경력", example = "15")
    private Integer career;
    @Schema(description = "시작 선호 시간", example = "06:00:00")
    private LocalTime preferTimeStart;
    @Schema(description = "종료 선호 시간", example = "21:00:00")
    private LocalTime preferTimeEnd;

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
