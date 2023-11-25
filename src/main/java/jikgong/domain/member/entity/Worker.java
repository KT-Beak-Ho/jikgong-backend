package jikgong.domain.member.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class Worker {
    private String workerName; // 노동자 이름
    private String rrnPrefix; // 생년월일 (주민등록번호 앞자리)
    @Enumerated(value = EnumType.STRING)
    private Gender gender; // 성별
    @Enumerated(value = EnumType.STRING)
    private Nationality nationality; // 국적
}
