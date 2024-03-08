package jikgong.domain.member.entity;

import jakarta.persistence.Embeddable;
import jikgong.domain.notification.dtos.company.CompanyNotificationInfoRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CompanyNotificationInfo {
    private Boolean companyOfferDecision; // 제안 결과 수신 여부
    private Boolean companyEvent; // 이벤트 수신 여부

    @Builder
    public CompanyNotificationInfo(Boolean isNotification) {
        // 회원 가입 시 isNotification 값이
        // true 라면 전부 수신 동의
        // false 라면 전부 수신 미동의
        this.companyOfferDecision = isNotification;
        this.companyEvent = isNotification;
    }

    public void updateInfo(CompanyNotificationInfoRequest request) {
        this.companyOfferDecision = request.getCompanyOfferDecision();
        this.companyEvent = request.getCompanyEvent();
    }
}
