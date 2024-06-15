package jikgong.domain.member.dto.join;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VerificationSmsResponse {
    @Schema(description = "인증 코드 6자리", example = "123456")
    private String authCode; // 6자리 인증 코드
}
