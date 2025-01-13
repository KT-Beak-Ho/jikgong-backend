package jikgong.domain.member.dto.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequest {

    @Schema(description = "로그인 아이디 또는 전화번호", example = "abcdefg1")
    private String loginIdOrPhone;
    @Schema(description = "로그인 패스워드", example = "abcdefg1")
    private String password;
    @Schema(description = "device token")
    private String deviceToken;
}
