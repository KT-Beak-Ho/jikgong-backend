package jikgong.domain.member.dto.join.verification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class JoinVerifyAccountRequest {

    @Schema(description = "계좌 번호", example = "12341234123412")
    private String account; // 계좌 번호
    @Schema(description = "은행 종류", example = "국민은행")
    private String bank; // 은행
}
