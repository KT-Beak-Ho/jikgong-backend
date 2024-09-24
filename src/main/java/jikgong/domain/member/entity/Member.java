package jikgong.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.history.entity.History;
import jikgong.domain.location.entity.Location;
import jikgong.domain.member.dto.info.CompanyInfoRequest;
import jikgong.domain.member.dto.info.WorkerInfoRequest;
import jikgong.domain.visaimage.entity.VisaImage;
import jikgong.domain.workexperience.entity.WorkExperience;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true)
    private String loginId; // 로그인 아이디
    private String password; // 패스워드

    @Column(unique = true)
    private String phone; // 휴대폰 번호

    @Column(unique = true)
    private String account; // 계좌
    private String bank; // 은행 종류
    private Boolean privacyConsent; // 개인정보 동의
    private String deviceToken; // 기기 토큰
    private Boolean isDeleted; // 회원 탈퇴 여부

    @Enumerated(value = EnumType.STRING)
    private Role role;
    @Embedded
    private Worker workerInfo;
    @Embedded
    private Company companyInfo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visa_image_id")
    private VisaImage visaImage; // 비자 사진

    // 양방향 매핑
    @OneToMany(mappedBy = "member")
    List<WorkExperience> workExperienceList = new ArrayList<>();
    @OneToMany(mappedBy = "member")
    List<Location> locationList = new ArrayList<>();
    @OneToMany(mappedBy = "member")
    List<History> historyList = new ArrayList<>();
    @OneToMany(mappedBy = "member")
    List<Apply> applyList = new ArrayList<>();

    @Override
    public String toString() {
        return "{ loginId=" + loginId + ", role=" + role + " }";
    }

    @Builder
    public Member(String loginId, String password, String phone, String account, String bank, Boolean privacyConsent,
        String deviceToken, Role role, Worker workerInfo, Company companyInfo) {
        this.loginId = loginId;
        this.password = password;
        this.phone = phone;
        this.account = account;
        this.bank = bank;
        this.privacyConsent = privacyConsent;
        this.deviceToken = deviceToken;
        this.isDeleted = false;
        this.role = role;
        this.workerInfo = workerInfo;
        this.companyInfo = companyInfo;

        this.workExperienceList = new ArrayList<>();
        this.locationList = new ArrayList<>();
        this.historyList = new ArrayList<>();
        this.applyList = new ArrayList<>();
    }

    public void updateDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void updateWorkerInfo(WorkerInfoRequest request) {
        this.workerInfo.updateWorkerInfo(request);
    }

    public void updateCompanyInfo(CompanyInfoRequest request) {
        this.companyInfo.updateWorkerInfo(request);
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    private void updateVisaExpiryDate(LocalDate visaExpiryDate) {
        this.workerInfo.updateVisaExpiryDate(visaExpiryDate);
    }

    public void updateVisaImage(VisaImage visaImage) {
        this.visaImage = visaImage;
    }
}
