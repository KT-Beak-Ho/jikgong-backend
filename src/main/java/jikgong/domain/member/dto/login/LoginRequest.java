package jikgong.domain.member.dto.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequest {

    @Schema(description = "로그인 아이디", example = "abcdefg1")
    private String loginId; // 인증 코드
    @Schema(description = "로그인 패스워드", example = "abcdefg1")
    private String password; // 인증 코드
    @Schema(description = "device token")
    private String deviceToken;
}
