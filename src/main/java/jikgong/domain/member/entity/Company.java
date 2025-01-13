package jikgong.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jikgong.domain.member.dto.info.CompanyInfoRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Company {

    private String bank; // 은행 종류
    private String account; // 계좌
    private String businessNumber; // 사업자 번호
    private String region; // 지역
    private String companyName; // 회사 명
    private String manager; // 담당자 이름
    @Column(columnDefinition = "TEXT")
    private String requestContent; // 문의 내용

    private CompanyNotificationInfo companyNotificationInfo; // 기업 알림 정보

    @Builder
    public Company(String bank, String account, String businessNumber, String region,
        String companyName, String manager, String requestContent, Boolean isNotification) {
        this.bank = bank;
        this.account = account;
        this.businessNumber = businessNumber;
        this.region = region;
        this.companyName = companyName;
        this.manager = manager;
        this.requestContent = requestContent;

        this.companyNotificationInfo = new CompanyNotificationInfo(isNotification);
    }

    public void updateCompanyInfo(CompanyInfoRequest request) {
        this.businessNumber = request.getBusinessNumber();
        this.region = request.getRegion();
        this.companyName = request.getCompanyName();
        this.manager = request.getManager();
    }
}
