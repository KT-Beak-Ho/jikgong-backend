package jikgong.domain.member.dto.join;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class ValidationLoginIdRequest {

    @Schema(description = "로그인 아이디", example = "abcdefg")
    private String loginId; // 휴대폰 번호
}
