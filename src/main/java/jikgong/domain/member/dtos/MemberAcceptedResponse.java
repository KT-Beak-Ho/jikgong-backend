package jikgong.domain.member.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jikgong.domain.history.entity.History;
import jikgong.domain.history.entity.WorkStatus;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.member.entity.Gender;
import jikgong.domain.member.entity.Member;
import jikgong.global.utils.AgeTransfer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields
public class MemberAcceptedResponse {
    /**
     * 인력 관리 화면에서 출퇴근 관련 dto
     */
    private Long historyId;
    private Long memberId;
    private String workerName; // 노동자 이름
    private String phone; // 휴대폰 번호
    private Integer age; // 나이
    private Gender gender; // 성별
    private WorkStatus startStatus; // 출근,결근 여부
    private WorkStatus endStatus; // 퇴근, 조퇴 여부


    public static MemberAcceptedResponse from (Apply apply) {
        Member member = apply.getMember();

        // 나이 계산
        int age = AgeTransfer.getAgeByBirth(member.getWorkerInfo().getBrith());

        return MemberAcceptedResponse.builder()
                .memberId(member.getId())
                .workerName(member.getWorkerInfo().getWorkerName())
                .phone(member.getPhone())
                .age(age)
                .gender(member.getWorkerInfo().getGender())
                .build();
    }

    public static MemberAcceptedResponse from (History history) {
        Member member = history.getMember();

        // 나이 계산
        int age = AgeTransfer.getAgeByBirth(member.getWorkerInfo().getBrith());

        return MemberAcceptedResponse.builder()
                .historyId(history.getId())
                .memberId(member.getId())
                .workerName(member.getWorkerInfo().getWorkerName())
                .phone(member.getPhone())
                .age(age)
                .gender(member.getWorkerInfo().getGender())
                .startStatus(history.getStartStatus())
                .endStatus(history.getEndStatus())
                .build();
    }

    public void updateHistoryInfo(History history) {
        this.historyId = history.getId();
        this.startStatus = history.getStartStatus();
        this.endStatus = history.getEndStatus();
    }
}
