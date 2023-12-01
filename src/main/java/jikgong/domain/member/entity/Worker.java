package jikgong.domain.member.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Worker {
    private String workerName; // 노동자 이름
    private String rrnPrefix; // 생년월일 (주민등록번호 앞자리)
    @Enumerated(value = EnumType.STRING)
    private Gender gender; // 성별
    private String nationality; // 국적

    @Builder
    public Worker(String workerName, String rrnPrefix, Gender gender, String nationality) {
        this.workerName = workerName;
        this.rrnPrefix = rrnPrefix;
        this.gender = gender;
        this.nationality = nationality;
    }
}
