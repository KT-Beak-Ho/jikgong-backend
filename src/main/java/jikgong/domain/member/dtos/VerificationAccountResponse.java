package jikgong.domain.member.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VerificationAccountResponse {
    private String authCode; // 2자리 인증 코드
}
