package jikgong.domain.jobpost.dto.company;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import jikgong.domain.jobpost.entity.Park;
import jikgong.domain.workexperience.entity.Tech;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
public class JobPostSaveRequest {

    @Schema(description = "공고 제목", example = "사하구  낙동5블럭  낙동강 온도 측정 센터 신축공사")
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
    @Schema(description = "시", example = "부산광역시")
    private String city; // 시
    @Schema(description = "구", example = "사하구")
    private String district; // 구

    // 리스트 정보
    @NotEmpty(message = "일하는 날짜 리스트는 최소한 하나의 날짜가 필요합니다.")
    @Schema(description = "일하는 날짜 리스트", example = "[\"2024-01-01\", \"2024-01-02\"]")
    private List<LocalDate> dateList;
    @Schema(description = "픽업 주소 리스트", example = "[\"부산광역시 사하구 낙동대로 550번길 37\", \"대한민국 부산광역시 서구 구덕로 225\"]")
    private List<String> pickupList;

    // 담당자 정보
    @Schema(description = "담당자 명", example = "홍길동")
    private String managerName;
    @Schema(description = "연락 번호", example = "01012345678")
    private String phone;

    // 모집 공고 상세 설명
    @Schema(description = "모집 공고 상세 설명", example = """
        주요업무
        (업무내용)
        - 건축구조물의 내·외벽, 바닥, 천장 등에 각종 장비를 사용해 타일을 시멘트 또는 기타 접착제로 붙여서 마감
        - 주택, 상업시설, 문화시설 등의 고품질화
        - 외벽, 바닥, 천정 등에 각종 도기류 및 화학 제품류의 타일을 접착
        """)
    private String description;

    // 프로젝트
    @Schema(description = "projectId", example = "1")
    private Long projectId;
}
