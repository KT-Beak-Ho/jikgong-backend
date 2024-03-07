package jikgong.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Company {

    private String businessNumber; // 사업자 번호
    private String region; // 지역
    private String companyName; // 회사 명
    private String email; // 이메일
    private String manager; // 담당자 이름
    @Column(columnDefinition = "TEXT")
    private String requestContent; // 문의 내용

    private CompanyNotificationInfo companyNotificationInfo;

    @Builder
    public Company(String businessNumber, String region, String companyName, String email, String manager, String requestContent, Boolean isNotification) {
        this.businessNumber = businessNumber;
        this.region = region;
        this.companyName = companyName;
        this.email = email;
        this.manager = manager;
        this.requestContent = requestContent;

        this.companyNotificationInfo = new CompanyNotificationInfo(isNotification);
    }
}
