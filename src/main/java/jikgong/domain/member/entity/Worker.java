package jikgong.domain.member.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
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
    private String brith; // 생년월일
    private String rrn; // 주민등록번호
    @Enumerated(value = EnumType.STRING)
    private Gender gender; // 성별
    private String nationality; // 국적
    private Boolean isOffer; // 헤드헌팅 여부

    @Embedded
    private WorkerNotificationInfo workerNotificationInfo;


    @Builder
    public Worker(String workerName, String birth, String rrn, Gender gender, String nationality, Boolean isNotification) {
        this.workerName = workerName;
        this.brith = birth;
        this.rrn = rrn;
        this.gender = gender;
        this.nationality = nationality;
        this.isOffer = true; // 기본 true
        this.workerNotificationInfo = new WorkerNotificationInfo(isNotification);
    }
}
