package jikgong.domain.member.entity;

import jakarta.persistence.*;
import jikgong.domain.certification.entity.Certification;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.member.dtos.JoinRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.jdbc.Work;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String phone;
    private String authCode; // 인증 코드 (비밀번호로 사용)

    @Enumerated(value = EnumType.STRING)
    private Role role;

    private String account; // 계좌
    private String bank; // 은행 종류
    private Boolean isDeleted; // 회원 탈퇴 여부

    @Embedded
    private Worker workerInfo;
    @Embedded
    private Company companyInfo;

    @OneToOne
    @JoinColumn(name = "certification_id")
    private Certification certification;

    @Builder
    public Member(String phone, String authCode, Role role, String account, String bank, Worker worker, Company company, Certification certification) {
        this.phone = phone;
        this.authCode = authCode;
        this.role = role;
        this.account = account;
        this.bank = bank;
        this.workerInfo = worker;
        this.companyInfo = company;
        this.certification = certification;
        this.isDeleted = false;
    }
}
