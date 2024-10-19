package jikgong.domain.member.dto.info;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PasswordFindResponse {

    private String temporaryPassword;

    public static PasswordFindResponse from(String temporaryPassword) {
        return PasswordFindResponse.builder()
            .temporaryPassword(temporaryPassword)
            .build();
    }
}
