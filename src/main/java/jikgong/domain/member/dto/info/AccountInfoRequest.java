package jikgong.domain.member.dto.info;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AccountInfoRequest {

    @Schema(description = "은행 종류", example = "부산은행")
    @NotBlank
    private String bank; // 은행
    @Schema(description = "예금주", example = "이순신")
    @NotBlank
    private String accountHolder; // 예금주
    @Schema(description = "계좌 번호", example = "6664342312854")
    @NotBlank
    private String account; // 게좌 번호
}
