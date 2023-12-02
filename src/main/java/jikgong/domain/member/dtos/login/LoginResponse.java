package jikgong.domain.member.dtos.login;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
}
