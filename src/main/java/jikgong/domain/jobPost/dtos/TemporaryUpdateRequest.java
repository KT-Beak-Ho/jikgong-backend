package jikgong.domain.jobPost.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jikgong.domain.jobPost.entity.Park;
import jikgong.domain.jobPost.entity.Tech;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@NoArgsConstructor
@Getter
public class TemporaryUpdateRequest {
    @Schema(description = "jobPostId", example = "1")
    private Long jobPostId;
    @Schema(description = "공고 제목", example = "사하구 낙동5블럭 낙동강 온도 측정 센터 신축공사")
    private String title;
    @Schema(description = "인부 타입", example = "NORMAL")
    private Tech tech;
    @Schema(description = "시작 일시", example = "09:30:00")
    private LocalTime startTime;
    @Schema(description = "종료 일시", example = "18:00:00")
    private LocalTime endTime;
    @Schema(description = "모집 인원", example = "60")
    private Integer recruitNum;
    @Schema(description = "임금", example = "150000")
    private Integer wage;
    @Schema(description = "준비 사항", example = "작업복, 작업화")
    private String preparation;
    @Schema(description = "모집 마감", example = "2024-03-01T09:30:00", type="string")
    private LocalDateTime expirationTime;
    @Schema(description = "주차 공간 설명", example = "2번 GateWay 옆 공간")
    private String parkDetail;

    // 가능 여부 정보
    @Schema(description = "식사 제공 여부", example = "true")
    private Boolean meal;
    @Schema(description = "픽업 여부", example = "true")
    private Boolean pickup;
    @Schema(description = "주차 가능 여부", example = "FREE")
    private Park park;

    // 위치 정보
    @Schema(description = "작업 장소 도로명 주소", example = "부산광역시 사하구 낙동대로 550번길 37")
    private String address; // 도로명 주소
    @Schema(description = "작업 장소 위도", example = "35.116777388697734")
    private Float latitude; // 위도
    @Schema(description = "작업 장소 경도", example = "128.9685393114043")
    private Float longitude; // 경도

    // 리스트 정보
    @NotEmpty(message = "일하는 날짜 리스트는 최소한 하나의 날짜가 필요합니다.")
    @Schema(description = "일하는 날짜 리스트", example = "[\"2024-01-01\", \"2024-01-02\"]")
    private List<LocalDate> workDateList;
    @Schema(description = "픽업 주소 리스트", example = "[\"부산광역시 사하구 낙동대로 550번길 37\", \"대한민국 부산광역시 서구 구덕로 225\"]")
    private List<String> pickupList;

    // 담당자 정보
    @Schema(description = "담당자 명", example = "홍길동")
    private String managerName;
    @Schema(description = "연락 번호", example = "01012345678")
    private String phone;

    // 프로젝트
    @Schema(description = "projectId", example = "1")
    private Long projectId;

}
