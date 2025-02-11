package jikgong.domain.member.dto.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class WorkerCardRequest {

    @Schema(description = "근로자 카드 번호", example = "11111111")
    private String workerCardNumber;
}
