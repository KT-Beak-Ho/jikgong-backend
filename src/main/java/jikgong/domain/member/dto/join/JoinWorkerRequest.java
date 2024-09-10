package jikgong.domain.member.dto.join;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import jikgong.domain.member.entity.Gender;
import jikgong.domain.member.entity.Nationality;
import jikgong.domain.member.entity.Role;
import jikgong.domain.workexperience.dto.WorkExperienceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
@ToString
public class JoinWorkerRequest {

    // 공통 정보
    @Schema(description = "로그인 아이디", example = "abcdefg1")
    private String loginId; // 인증 코드
    @Schema(description = "로그인 패스워드", example = "abcdefg1")
    private String password; // 인증 코드
    @Schema(description = "휴대폰 번호", example = "01012345678")
    private String phone;
    @Schema(description = "회원 타입 [ROLE_WORKER or ROLE_REGISTER]", example = "ROLE_WORKER")
    private Role role; // 회원 타입
    @Schema(description = "계좌 번호", example = "12341234123412")
    private String account; // 계좌 번호
    @Schema(description = "은행 종류", example = "국민은행")
    private String bank; // 은행
    @Schema(description = "device token", example = "token")
    private String deviceToken; // 기기 토큰
    @Schema(description = "알림 수신 여부", example = "true")
    private Boolean isNotification; // 알림 수신 여부

    // 노동자 정보
    @Schema(description = "노동자 이름", example = "홍길동")
    private String workerName; // 노동자 이름
    @Schema(description = "생년월일", example = "19750101")
    private String birth; // 생년월일
    @Schema(description = "주민등록번호", example = "750101-1752442")
    private String rrn; // 생년월일
    @Schema(description = "성별 [MALE or FEMAILE]", example = "MALE")
    private Gender gender; // 성별
    @Schema(description = "국적 [KOREAN or FOREIGNER]", example = "KOREAN")
    private Nationality nationality; // 국적
    @Schema(description = "비자 여부", example = "true")
    private Boolean hasVisa; // 비자 여부
    @Schema(description = "교육 이수증 여부", example = "true")
    private Boolean hasEducationCertificate; // 교육 이수증 여부
    @Schema(description = "근로자 카드 여부", example = "true")
    private Boolean hasWorkerCard; // 근로자 카드 여부
    // 경력 정보
    @Schema(description = "경력 정보", example = "[{ \"tech\": \"NORMAL\", \"experienceMonths\": 24 }, { \"tech\": \"LANDSCAPER\", \"experienceMonths\": 18 }]")
    private List<WorkExperienceRequest> workExperienceRequest;

    // 위치 정보
    @Schema(description = "도로명 주소", example = "부산광역시 사하구 낙동대로 550번길 37")
    private String address; // 도로명 주소
    @Schema(description = "위도", example = "35.116777388697734")
    private Float latitude; // 위도
    @Schema(description = "경도", example = "128.9685393114043")
    private Float longitude; // 경도
}
