package jikgong.domain.member.dto.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PasswordFindRequest {

    @Schema(description = "로그인 아이디", example = "abcdefg1")
    private String loginId;
    @Schema(description = "휴대폰 번호", example = "01011111111")
    private String phone;
}
