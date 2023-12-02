package jikgong.domain.member.dtos.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RefreshTokenRequest {
    @Schema(description = "refresh token", example = "refresh_token")
    private String refreshToken;
}
