package jikgong.domain.member.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VerificationSmsResponse {
    @Schema(description = "6자리 인증 코드", example = "")
    private String authCode; // 6자리 인증 코드
}
