package jikgong.domain.member.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VerificationAccountResponse {
    @Schema(description = "인증 번호", example = "")
    private String authCode; // 2자리 인증 코드
}
