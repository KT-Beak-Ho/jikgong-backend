package jikgong.domain.notification.dto.worker;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class WorkerNotificationInfoRequest {
    @Schema(description = "제안 수신 여부", example = "false")
    private Boolean workerOffer; // 제안 수신 여부
    @Schema(description = "지원 결과 수신 여부", example = "false")
    private Boolean workerApplyDecision; // 지원 결과 수신 여부
    @Schema(description = "이벤트 수신 여부", example = "false")
    private Boolean workerEvent; // 이벤트 수신 여부
}
