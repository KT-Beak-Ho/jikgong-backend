package jikgong.domain.member.dto.info;

import java.util.List;
import java.util.stream.Collectors;
import jikgong.domain.member.entity.Gender;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.entity.Nationality;
import jikgong.domain.workexperience.dto.WorkExperienceResponse;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WorkerInfoResponse {

    private String loginId; // 로그인 아이디
    private String phone; // 휴대폰 번호
    private String email;

    private String workerName; // 노동자 이름
    private String birth; // 생년월일
    private Gender gender; // 성별
    private Nationality nationality; // 국적
    private String account; // 게좌 번호
    private String bank; // 은행
    private Boolean hasVisa; // 비자 여부 (외국인만 입력)
    private Boolean hasEducationCertificate; // 교육 증명서
    private Boolean hasWorkerCard; // 근로자 카드 여부

    private List<WorkExperienceResponse> workExperienceResponseList; // 경력 정보

    public static WorkerInfoResponse from(Member worker) {
        List<WorkExperienceResponse> workExperienceResponseList = worker.getWorkExperienceList().stream()
            .map(WorkExperienceResponse::from)
            .collect(Collectors.toList());

        return WorkerInfoResponse.builder()
            .loginId(worker.getLoginId())
            .phone(worker.getPhone())
            .email(worker.getEmail())

            .workerName(worker.getWorkerInfo().getWorkerName())
            .birth(worker.getWorkerInfo().getBirth())
            .gender(worker.getWorkerInfo().getGender())
            .nationality(worker.getWorkerInfo().getNationality())
            .account(worker.getWorkerInfo().getAccount())
            .bank(worker.getWorkerInfo().getBank())

            .hasVisa(worker.getWorkerInfo().getHasVisa())
            .hasEducationCertificate(worker.getWorkerInfo().getHasEducationCertificate())
            .hasWorkerCard(worker.getWorkerInfo().getHasWorkerCard())

            .workExperienceResponseList(workExperienceResponseList)

            .build();
    }
}
