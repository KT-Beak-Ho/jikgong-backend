package jikgong.domain.member.entity;

import jakarta.persistence.Embeddable;
import jikgong.domain.notification.dtos.worker.WorkerNotificationInfoRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class WorkerNotificationInfo {
    private Boolean workerOffer; // 제안 수신 여부
    private Boolean workerApplyDecision; // 지원 결과 수신 여부
    private Boolean workerEvent; // 이벤트 수신 여부

    @Builder
    public WorkerNotificationInfo(Boolean isNotification) {
        // 회원 가입 시 isNotification 값이
        // true 라면 전부 수신 동의
        // false 라면 전부 수신 미동의
        this.workerOffer = isNotification;
        this.workerApplyDecision = isNotification;
        this.workerEvent = isNotification;
    }

    public void updateInfo(WorkerNotificationInfoRequest request) {
        this.workerOffer = request.getWorkerOffer();
        this.workerApplyDecision = request.getWorkerApplyDecision();
        this.workerEvent = request.getWorkerEvent();
    }
}
