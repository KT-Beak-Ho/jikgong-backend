package jikgong.domain.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Address {
    @Schema(description = "작업 장소 도로명 주소", example = "부산광역시 사하구 낙동대로 550번길 37")
    private String address; // 도로명 주소
    @Schema(description = "작업 장소 위도", example = "35.116777388697734")
    private Float latitude; // 위도
    @Schema(description = "작업 장소 경도", example = "128.9685393114043")
    private Float longitude; // 경도
}
