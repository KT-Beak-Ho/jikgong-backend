package jikgong.domain.notification.dtos.company;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CompanyNotificationInfoRequest {
    @Schema(description = "// 제안 결과 수신 여부", example = "false")
    private Boolean companyOfferDecision; // 제안 결과 수신 여부
    @Schema(description = "이벤트 수신 여부", example = "false")
    private Boolean companyEvent; // 이벤트 수신 여부
}
