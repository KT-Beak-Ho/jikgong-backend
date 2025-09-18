package jikgong.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.history.entity.History;
import jikgong.domain.location.entity.Location;
import jikgong.domain.member.dto.info.AccountInfoRequest;
import jikgong.domain.member.dto.info.CompanyInfoRequest;
import jikgong.domain.member.dto.info.WorkerCardRequest;
import jikgong.domain.member.dto.info.WorkerInfoRequest;
import jikgong.domain.member.dto.join.JoinCompanyRequest;
import jikgong.domain.member.dto.join.JoinWorkerRequest;
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

    @Column(unique = true, nullable = false)
    private String loginId; // 로그인 아이디
    @Column(nullable = false)
    private String password; // 패스워드

    @Column(unique = true, nullable = false)
    private String phone; // 휴대폰 번호
    @Column(unique = true, nullable = false)
    private String email; // 이메일

    private Boolean privacyConsent; // 개인정보 동의 여부

    private String signatureImagePath;

    private String deviceToken; // 기기 토큰
    private Boolean isDeleted; // 회원 탈퇴 여부

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @Embedded
    private Worker workerInfo;
    @Embedded
    private Company companyInfo;

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
    public Member(String loginId, String password, String phone, String email,
        Boolean privacyConsent, String signatureImagePath,
        String deviceToken, Role role, Worker workerInfo, Company companyInfo) {
        this.loginId = loginId;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.privacyConsent = privacyConsent;
        this.signatureImagePath = signatureImagePath;
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

    public static Member createMember(JoinWorkerRequest request, String encodedPassword,
        Worker worker) {
        return Member.builder()
            .loginId(request.getLoginId())
            .password(encodedPassword)
            .phone(request.getPhone())
            .email(request.getEmail())
            .privacyConsent((request.getPrivacyConsent()))
            .role(request.getRole())
            .deviceToken(request.getDeviceToken())
            .workerInfo(worker)
            .build();
    }

    public static Member createMember(JoinCompanyRequest request, String encodedPassword,
        Company company) {
        return Member.builder()
            .loginId(request.getLoginId())
            .password(encodedPassword)
            .phone(request.getPhone())
            .email(request.getEmail())
            .privacyConsent(true)
            .role(request.getRole())
            .deviceToken(request.getDeviceToken())
            .companyInfo(company)
            .build();
    }

    public void updateDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }


    public void updateCompanyInfo(CompanyInfoRequest request) {
        this.email = request.getEmail();
        this.companyInfo.updateCompanyInfo(request);
    }

    public void updateWorkerInfo(WorkerInfoRequest request) {
        this.email = request.getEmail();
        this.workerInfo.updateWorkerInfo(request);
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateSignatureImagePath(String signatureImagePath) {
        this.signatureImagePath = signatureImagePath;
    }

    public void updateAccountInfo(AccountInfoRequest request) {
        this.workerInfo.updateAccountInfo(request);
    }

    public void updateEducationCertificateImagePath(String educationCertificateImagePath) {
        this.workerInfo.updateEducationCertificateImgPath(educationCertificateImagePath);
    }

    public void updateWorkerCard(String workerCardImagePath, WorkerCardRequest request) {
        this.workerInfo.updateWorkerCardImgPath(workerCardImagePath, request.getWorkerCardNumber());
    }
}
