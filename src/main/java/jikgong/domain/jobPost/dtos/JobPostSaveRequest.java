package jikgong.domain.jobPost.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jikgong.domain.jobPost.entity.Tech;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
public class JobPostSaveRequest {
    @Schema(description = "인부 타입", example = "NORMAL")
    private Tech tech;
    @Schema(description = "시작 일시", example = "2023-12-25T09:30:00")
    private LocalDateTime startTime;
    @Schema(description = "모집 인원", example = "60")
    private Integer recruitNum;
    @Schema(description = "임금", example = "150000")
    private Integer wage;
    @Schema(description = "작업 상세", example = "작업 상세")
    private String workDetail;
    @Schema(description = "준비 사항", example = "작업복, 작업화")
    private String preparation;
    @Schema(description = "모집 마감", example = "2023-12-25T09:30:00")
    private LocalDateTime expirationTime;

    // 가능 여부 정보
    @Schema(description = "식사 제공 여부", example = "true")
    private Boolean meal;
    @Schema(description = "픽업 여부", example = "true")
    private Boolean pickup;
    @Schema(description = "안전 장비 제공 여부", example = "true")
    private Boolean safeEquipment;
    @Schema(description = "주차 가능 여부", example = "true")
    private Boolean park;

    // 위치 정보
    @Schema(description = "도로명 주소", example = "부산광역시 사하구 낙동대로 550번길 37")
    private String address; // 도로명 주소
    @Schema(description = "위도", example = "35.116777388697734")
    private Float latitude; // 위도
    @Schema(description = "경도", example = "128.9685393114043")
    private Float longitude; // 경도

    // 픽업 정보
    @Schema(description = "픽업 주소 리스트", example = "[\"부산광역시 사하구 낙동대로 550번길 37\", \"대한민국 부산광역시 서구 구덕로 225\"]")
    private List<String> pickupList;
}
