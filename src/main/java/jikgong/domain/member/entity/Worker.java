package jikgong.domain.member.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jikgong.domain.member.dto.info.AccountInfoRequest;
import jikgong.domain.member.dto.info.WorkerInfoRequest;
import jikgong.domain.member.dto.join.JoinWorkerRequest;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
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
    @Enumerated(value = EnumType.STRING)
    private Gender gender; // 성별
    private Nationality nationality; // 국적
    private String bank; // 은행 종류
    private String accountHolder; // 예금주
    private String account; // 계좌

    private String educationCertificateImgPath; // 교육 증명서 이미지 경로
    private String workerCardImgPath; // 근로자 카드 이미지 경로
    private String workerCardNumber; // 근로자 카드 번호

    private Float score; // 평점

    private Boolean credentialLiabilityConsent; // 자격증명 법적 책임 동의 여부
    private Boolean isOffer; // 헤드헌팅 여부

    @Embedded
    private WorkerNotificationInfo workerNotificationInfo; // 노동자 알림 정보

    @Builder
    public Worker(String workerName, String birth, Gender gender, Nationality nationality,
        String bank, String accountHolder, String account, Float score,
        Boolean credentialLiabilityConsent, Boolean isNotification) {
        this.workerName = workerName;
        this.birth = birth;
        this.gender = gender;
        this.nationality = nationality;
        this.bank = bank;
        this.accountHolder = accountHolder;
        this.account = account;
        this.score = score;
        this.credentialLiabilityConsent = credentialLiabilityConsent;
        this.isOffer = true; // 기본 true
        this.workerNotificationInfo = new WorkerNotificationInfo(isNotification);
    }

    public static Worker createWorker(JoinWorkerRequest request) {
        return Worker.builder()
            .workerName(request.getWorkerName())
            .birth(request.getBirth())
            .gender(request.getGender())
            .nationality(request.getNationality())
            .bank(request.getBank())
            .accountHolder(request.getAccountHolder())
            .account(request.getAccount())
            .score(3.0F) // 평점은 3.0으로 시작
            .credentialLiabilityConsent(request.getCredentialLiabilityConsent())
            .isNotification(request.getIsNotification())
            .build();
    }

    public void updateWorkerInfo(WorkerInfoRequest request) {
        this.workerName = request.getWorkerName();
        this.birth = request.getBirth();
        this.gender = request.getGender();
        this.nationality = request.getNationality();
    }

    public void updateEducationCertificateImgPath(String educationCertificateImgPath) {
        this.educationCertificateImgPath = educationCertificateImgPath;
    }

    public void updateWorkerCardImgPath(String workerCardImgPath, String workerCardNumber) {
        if (workerCardImgPath == null || workerCardNumber == null) {
            throw new JikgongException(ErrorCode.MEMBER_UPDATE_WORKER_CARD_FAIL);
        }
        this.workerCardImgPath = workerCardImgPath;
        this.workerCardNumber = workerCardNumber;
    }

    public void updateAccountInfo(AccountInfoRequest request) {
        this.bank = request.getBank();
        this.account = request.getAccount();
        this.accountHolder = request.getAccountHolder();
    }
}
