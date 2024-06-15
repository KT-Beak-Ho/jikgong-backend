package jikgong.domain.member.dto.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class RefreshTokenRequest {

    @Schema(description = "refresh token", example = "refresh_token")
    private String refreshToken;
}
