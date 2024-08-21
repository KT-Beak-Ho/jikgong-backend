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
import java.util.ArrayList;
import java.util.List;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.certification.entity.Certification;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.history.entity.History;
import jikgong.domain.location.entity.Location;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseEntity {

    // todo: 비자 필드 추가해야 할 듯, 

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
    private String deviceToken; // 기기 토큰
    private Boolean isDeleted; // 회원 탈퇴 여부

    @Enumerated(value = EnumType.STRING)
    private Role role;
    @Embedded
    private Worker workerInfo;
    @Embedded
    private Company companyInfo;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "certification_id")
    private Certification certification;

    // 양방향 매핑
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
    public Member(String loginId, String password, String phone, String account, String bank, String deviceToken,
        Role role, Worker workerInfo, Company companyInfo, Certification certification) {
        this.loginId = loginId;
        this.password = password;
        this.phone = phone;
        this.account = account;
        this.bank = bank;
        this.deviceToken = deviceToken;
        this.isDeleted = false;
        this.role = role;
        this.workerInfo = workerInfo;
        this.companyInfo = companyInfo;
        this.certification = certification;

        this.locationList = new ArrayList<>();
        this.historyList = new ArrayList<>();
        this.applyList = new ArrayList<>();
    }

    public void setCertification(Certification certification) {
        this.certification = certification;
    }

    public void updateDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
