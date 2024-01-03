package jikgong.domain.member.dtos.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LoginRequest {
    @Schema(description = "휴대폰 번호", example = "01012345678")
    private String phone;
    @Schema(description = "인증 코드 6자리", example = "123456")
    private String authCode;
    @Schema(description = "device token")
    private String deviceToken;
}
