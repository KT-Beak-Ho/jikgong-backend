package jikgong.domain.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jikgong.domain.common.Address;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@ToString
@Builder
@AllArgsConstructor // InitDB 에 사용
public class ProjectSaveRequest {

    @Schema(description = "프로젝트 명", example = "사하구 낙동5블럭 낙동강 온도 측정 센터 신축공사")
    private String projectName; // 프로젝트 명
    @Schema(description = "프로젝트 착공일", example = "2024-01-01")
    private LocalDate startDate; // 착공일
    @Schema(description = "프로젝트 준공일", example = "2024-03-01")
    private LocalDate endDate; // 준공일
    @Schema(description = "프로젝트 설명", example = "예시용으로 사용할 프로젝트.")
    private String description;
    @Schema(description = "프로젝트 위치 정보")
    private Address location;
}
