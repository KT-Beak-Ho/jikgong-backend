package jikgong.domain.member.dto.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LoginIdAuthCodeRequest {

    @Schema(description = "휴대폰 번호", example = "01011111111")
    private String phone;
    @Schema(description = "인증 코드 6자리", example = "123456")
    private String authCode; // 6자리 인증 코드
}
