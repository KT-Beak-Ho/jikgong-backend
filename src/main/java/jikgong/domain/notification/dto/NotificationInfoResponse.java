package jikgong.domain.notification.dto;

import jikgong.domain.member.entity.CompanyNotificationInfo;
import jikgong.domain.member.entity.WorkerNotificationInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class NotificationInfoResponse {
    // 기업
    private Boolean companyOfferDecision; // 제안 결과 수신 여부
    private Boolean companyEvent; // 이벤트 수신 여부

    // 노동자
    private Boolean workerOffer; // 제안 수신 여부
    private Boolean workerApplyDecision; // 지원 결과 수신 여부
    private Boolean workerEvent; // 이벤트 수신 여부


    public static NotificationInfoResponse from(CompanyNotificationInfo info) {
        return NotificationInfoResponse.builder()
                .companyOfferDecision(info.getCompanyOfferDecision())
                .companyEvent(info.getCompanyEvent())
                .build();
    }

    public static NotificationInfoResponse from(WorkerNotificationInfo info) {
        return NotificationInfoResponse.builder()
                .workerOffer(info.getWorkerOffer())
                .workerApplyDecision(info.getWorkerApplyDecision())
                .workerEvent(info.getWorkerEvent())
                .build();
    }
}
