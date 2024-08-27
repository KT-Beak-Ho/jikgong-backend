package jikgong.domain.member.dto.info;

import jikgong.domain.member.entity.Gender;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.entity.Nationality;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WorkerInfoResponse {

    private String loginId; // 로그인 아이디
    private String phone; // 휴대폰 번호
    private String account; // 계좌
    private String bank; // 은행 종류

    private String workerName; // 노동자 이름
    private String birth; // 생년월일
    private String rrn; // 주민등록번호
    private Gender gender; // 성별
    private Nationality nationality; // 국적

    public static WorkerInfoResponse from(Member worker) {
        return WorkerInfoResponse.builder()
            .loginId(worker.getLoginId())
            .phone(worker.getPhone())
            .account(worker.getAccount())
            .bank(worker.getBank())

            .workerName(worker.getWorkerInfo().getWorkerName())
            .birth(worker.getWorkerInfo().getBirth())
            .rrn(worker.getWorkerInfo().getRrn())
            .gender(worker.getWorkerInfo().getGender())
            .nationality(worker.getWorkerInfo().getNationality())

            .build();
    }
}
