package jikgong.domain.member.dto.join;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jikgong.domain.member.entity.Role;
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
public class JoinCompanyRequest {

    // 공통 정보
    @Schema(description = "로그인 아이디", example = "abcdefg1")
    @NotBlank
    private String loginId; // 인증 코드
    @Schema(description = "로그인 패스워드", example = "abcdefg1")
    @NotBlank
    private String password; // 인증 코드
    @Schema(description = "휴대폰 번호", example = "01012345678")
    @NotBlank
    private String phone;
    @Schema(description = "회원 타입 [ROLE_WORKER or ROLE_COMPANY]", example = "ROLE_COMPANY")
    @NotNull
    private Role role; // 회원 타입
    @Schema(description = "device token", example = "token")
    private String deviceToken; // 기기 토큰

    // 회사 정보
    @Schema(description = "사업자 번호", example = "00000000")
    @NotBlank
    private String businessNumber; // 사업자 번호
    @Schema(description = "지역", example = "서울")
    @NotBlank
    private String region; // 지역
    @Schema(description = "회사 명", example = "삼성")
    @NotBlank
    private String companyName; // 회사 명
    @Schema(description = "이메일", example = "jaeyoung@naver.com")
    @NotBlank
    private String email; // 이메일
    @Schema(description = "담당자 이름", example = "이재용")
    @NotBlank
    private String manager; // 담당자 이름
    @Schema(description = "문의 내용", example = "직공 서비스에 가입하고 싶습니다.")
    private String inquiry; // 문의 내용
}
