package jikgong.domain.project.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@ToString
@Builder @AllArgsConstructor // InitDB 에 사용
public class ProjectSaveRequest {
    @Schema(description = "프로젝트 명", example = "사하구 낙동5블럭 낙동강 온도 측정 센터 신축공사 ")
    private String name; // 프로젝트 명
    @Schema(description = "프로젝트 착공일", example = "2024-01-01")
    private LocalDate startDate; // 착공일
    @Schema(description = "프로젝트 준공일", example = "2024-03-01")
    private LocalDate endDate; // 준공일

    // 위치 정보
    @Schema(description = "작업 장소 도로명 주소", example = "부산광역시 사하구 낙동대로 550번길 37")
    private String address; // 도로명 주소
    @Schema(description = "작업 장소 위도", example = "35.116777388697734")
    private Float latitude; // 위도
    @Schema(description = "작업 장소 경도", example = "128.9685393114043")
    private Float longitude; // 경도
}
