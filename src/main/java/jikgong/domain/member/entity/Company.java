package jikgong.domain.member.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class Company {
    private String companyName; // 회사명
    private String businessNumber; // 사업자 번호
}
