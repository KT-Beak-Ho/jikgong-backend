package jikgong.domain.member.entity;

import jakarta.persistence.*;
import jikgong.domain.certification.entity.Certification;
import jikgong.domain.common.BaseEntity;
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

    private String username;
    private String password;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    private String phone;
    private String account; // 계좌
    private String bank; // 은행 종류

    @Embedded
    private Worker worker;
    @Embedded
    private Company company;

    @OneToOne
    @JoinColumn(name = "certification_id")
    private Certification certification;

    @Builder
    public Member(String username, String password, Role role, String phone, String account, String bank, Worker worker, Company company, Certification certification) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.phone = phone;
        this.account = account;
        this.bank = bank;
        this.worker = worker;
        this.company = company;
        this.certification = certification;
    }
}
