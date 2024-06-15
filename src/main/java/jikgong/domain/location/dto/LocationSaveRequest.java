package jikgong.domain.location.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class LocationSaveRequest {

    @Schema(description = "도로명 주소", example = "부산광역시 사하구 낙동대로 550번길 37")
    private String address; // 도로명 주소
    @Schema(description = "위도", example = "35.116777388697734")
    private Float latitude; // 위도
    @Schema(description = "경도", example = "128.9685393114043")
    private Float longitude; // 경도
    @Schema(description = "대표 위치 여부", example = "false")
    private Boolean isMain; // 대표 위치 여부
}
