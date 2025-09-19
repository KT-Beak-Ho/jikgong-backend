package jikgong.domain.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jikgong.domain.common.Address;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@ToString
public class ProjectUpdateRequest {

    @Schema(description = "프로젝트 명", example = "사하구 낙동5블럭 낙동강 온도 측정 센터 신축공사 ")
    private String title;
    @Schema(description = "프로젝트 착공일", example = "2024-01-01")
    private LocalDate startDate;
    @Schema(description = "프로젝트 준공일", example = "2024-03-01")
    private LocalDate endDate;
    @Schema(description = "프로젝트 설명", example = "예시용으로 사용할 프로젝트.")
    private String description;
    @Schema(description = "프로젝트 상태", example = "???????")
    private String status;

    // 위치 정보
    Address location;
}
