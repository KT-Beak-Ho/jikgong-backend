package jikgong.domain.member.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jikgong.domain.member.dto.info.WorkerInfoRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Worker {

    private String workerName; // 노동자 이름
    private String birth; // 생년월일
    private String rrn; // 주민등록번호
    @Enumerated(value = EnumType.STRING)
    private Gender gender; // 성별
    private Nationality nationality; // 국적
    private Boolean hasVisa; // 비자 여부 (외국인만 입력)
    private Boolean hasEducationCertificate; // 교육 증명서
    private Boolean hasWorkerCard; // 근로자 카드 여부

    private Boolean isOffer; // 헤드헌팅 여부

    @Embedded
    private WorkerNotificationInfo workerNotificationInfo; // 노동자 알림 정보

    @Builder
    public Worker(String workerName, String birth, String rrn, Gender gender, Nationality nationality,
        Boolean hasVisa, Boolean hasEducationCertificate, Boolean hasWorkerCard, Boolean isNotification) {
        this.workerName = workerName;
        this.birth = birth;
        this.rrn = rrn;
        this.gender = gender;
        this.nationality = nationality;
        this.hasVisa = hasVisa;
        this.hasEducationCertificate = hasEducationCertificate;
        this.hasWorkerCard = hasWorkerCard;
        this.isOffer = true; // 기본 true
        this.workerNotificationInfo = new WorkerNotificationInfo(isNotification);
    }

    public void updateWorkerInfo(WorkerInfoRequest request) {
        this.workerName = request.getWorkerName();
        this.birth = request.getBirth();
        this.rrn = request.getRrn();
        this.gender = request.getGender();
        this.nationality = request.getNationality();
        this.hasVisa = request.getHasVisa();
        this.hasEducationCertificate = request.getHasEducationCertificate();
        this.hasWorkerCard = request.getHasWorkerCard();
    }
}
