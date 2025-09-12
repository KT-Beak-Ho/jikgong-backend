package jikgong.domain.member.dto.join;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import jikgong.domain.member.entity.Gender;
import jikgong.domain.member.entity.Nationality;
import jikgong.domain.member.entity.Role;
import jikgong.domain.workexperience.dto.WorkExperienceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
@ToString
public class JoinWorkerRequest {

    // 공통 정보
    @Schema(description = "로그인 아이디", example = "abcdefg1")
    @NotBlank
    private String loginId;
    @Schema(description = "로그인 패스워드", example = "abcdefg1")
    @NotBlank
    private String password;
    @Schema(description = "휴대폰 번호", example = "01012345678")
    @NotBlank
    private String phone;
    @Schema(description = "이메일", example = "gildong@gmail.com")
    @Email
    private String email; // 이메일
    @Schema(description = "회원 타입 [ROLE_WORKER or ROLE_REGISTER]", example = "ROLE_WORKER")
    @NotNull
    private Role role; // 회원 타입
    @Schema(description = "개인 정보 동의 여부", example = "true")
    @AssertTrue
    @NotNull
    private Boolean privacyConsent; // 개인 정보 동의 여부
    @Schema(description = "device token", example = "token")
    private String deviceToken; // 기기 토큰
    @Schema(description = "알림 수신 여부", example = "true")
    @NotNull
    private Boolean isNotification; // 알림 수신 여부

    // 노동자 정보
    @Schema(description = "노동자 이름", example = "홍길동")
    @NotBlank
    private String workerName; // 노동자 이름
    @Schema(description = "생년월일", example = "19750101")
    @NotBlank
    private String birth; // 생년월일
    @Schema(description = "성별 [MALE or FEMAILE]", example = "MALE")
    @NotNull
    private Gender gender; // 성별
    @Schema(description = "국적 [KOREAN or FOREIGNER]", example = "KOREAN")
    @NotNull
    private Nationality nationality; // 국적
    @Schema(description = "예금주", example = "홍길동")
    @NotBlank
    private String accountHolder; // 예금주
    @Schema(description = "계좌 번호", example = "12341234123412")
    @NotBlank
    private String account; // 게좌 번호
    @Schema(description = "은행 종류", example = "국민은행")
    @NotBlank
    private String bank; // 은행
    @Schema(description = "건설 노동자 카드 번호", example = "null")
    private String workerCardNumber;
    @Schema(description = "자격증명 법적 책임 동의 여부", example = "true")
    @AssertTrue
    @NotNull
    private Boolean credentialLiabilityConsent; // 자격증명 법적 책임 동의 여부

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

