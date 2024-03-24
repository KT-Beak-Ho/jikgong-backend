package jikgong.domain.member.dtos.join;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class ValidationLoginIdRequest {
    @Schema(description = "로그인 아이디", example = "abcdefg")
    private String loginId; // 휴대폰 번호
}
