package jikgong.domain.member.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ValidationUsernameRequest {
    private String username; // 아이디
}
