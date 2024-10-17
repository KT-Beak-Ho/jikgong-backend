package jikgong.domain.member.dto.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class VerificationFindLoginIdRequest {

    @Schema(description = "이름", example = "김진수")
    private String name;
    @Schema(description = "휴대폰 번호", example = "01011111111")
    private String phone;
    @Schema(description = "노동자: true  |  기업: false", example = "true")
    private Boolean isWorker;
}
