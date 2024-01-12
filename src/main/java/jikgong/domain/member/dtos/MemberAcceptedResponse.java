package jikgong.domain.member.dtos;

import jikgong.domain.history.entity.History;
import jikgong.domain.history.entity.WorkStatus;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.member.entity.Member;
import jikgong.global.utils.AgeTransfer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
@Builder
public class MemberAcceptedResponse {
    private Long memberId;
    private String workerName; // 노동자 이름
    private String phone; // 휴대폰 번호
    private Integer age; // 나이
    private WorkStatus workStatus; // 출근,결근 여부

    public static MemberAcceptedResponse fromApply(Apply apply) {
        Member member = apply.getMember();

        // 나이 계산
        int age = AgeTransfer.getAgeByRrn(member.getWorkerInfo().getBrith());

        return MemberAcceptedResponse.builder()
                .memberId(member.getId())
                .workerName(member.getWorkerInfo().getWorkerName())
                .phone(member.getPhone())
                .age(age)
                .build();
    }

    public static MemberAcceptedResponse fromHistory(History history) {
        Member member = history.getMember();

        // 나이 계산
        int age = AgeTransfer.getAgeByRrn(member.getWorkerInfo().getBrith());

        return MemberAcceptedResponse.builder()
                .memberId(member.getId())
                .workerName(member.getWorkerInfo().getWorkerName())
                .phone(member.getPhone())
                .age(age)
                .workStatus(history.getIsWork() ? WorkStatus.WORK : WorkStatus.NOT_WORK)
                .build();
    }
}