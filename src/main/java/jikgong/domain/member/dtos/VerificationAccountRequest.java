package jikgong.domain.member.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class VerificationAccountRequest {
    private String account; // 계좌 번호
    private String bank; // 은행
}
