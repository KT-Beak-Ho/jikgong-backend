package jikgong.domain.member.dto.join.verification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JoinVerifyAccountResponse {

    @Schema(description = "인증 번호", example = "12")
    private String authCode; // 2자리 인증 코드
}
