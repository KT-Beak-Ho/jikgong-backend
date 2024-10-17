package jikgong.domain.member.dto.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PasswordUpdateRequest {

    @Schema(description = "현재 비밀번호", example = "abcdefg1")
    private String currentPassword;
    @Schema(description = "새로운 비밀번호", example = "abcdefg2")
    private String newPassword;
}
