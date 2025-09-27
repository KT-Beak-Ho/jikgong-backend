package jikgong.domain.member.dto.join.validation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class JoinValidateEmailRequest {

    @Schema(description = "email", example = "abcdefg@jikgong.com")
    private String email; // 휴대폰 번호
}
