package jikgong.domain.member.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class VerificationSmsRequest {
    private String phone; // 핸드폰 번호
}
