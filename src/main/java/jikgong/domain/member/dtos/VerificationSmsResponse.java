package jikgong.domain.member.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VerificationSmsResponse {
    private String authCode; // 6자리 인증 코드
}
