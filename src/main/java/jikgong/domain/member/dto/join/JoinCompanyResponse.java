package jikgong.domain.member.dto.join;

import jikgong.domain.member.dto.login.LoginResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class JoinCompanyResponse {
    private Long companyId;
    private String accessToken;
    private String refreshToken;

    public static JoinCompanyResponse from(Long companyId, LoginResponse loginResponse) {
        return JoinCompanyResponse.builder()
                .companyId(companyId)
                .accessToken(loginResponse.getAccessToken())
                .refreshToken(loginResponse.getRefreshToken())
                .build();
    }
}
