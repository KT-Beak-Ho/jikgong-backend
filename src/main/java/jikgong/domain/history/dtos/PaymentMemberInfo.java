package jikgong.domain.history.dtos;

import jikgong.domain.history.entity.History;
import jikgong.domain.history.entity.WorkStatus;
import jikgong.domain.member.entity.Gender;
import jikgong.domain.member.entity.Member;
import jikgong.global.utils.AgeTransfer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class PaymentMemberInfo {
    private Long memberId;
    private String workerName; // 노동자 명
    private Integer age; // 나이
    private Gender gender; // 성별

    private Integer payment; // 지급 금액
    private WorkStatus startStatus; // 출근, 결근
    private WorkStatus endStatus; // 퇴근, 조퇴

    public static PaymentMemberInfo from(History history) {
        Member member = history.getMember();
        WorkStatus workStatus = history.getStartStatus();
        int payment = 0;

        // 출근 기록만 있는 경우 or 결근일 경우 => 0원
        if (workStatus == WorkStatus.START_WORK || workStatus == WorkStatus.NOT_WORK) {
            payment = 0;
        }
        // 퇴근, 조퇴일 경우 => 임금 지급
        if (workStatus == WorkStatus.FINISH_WORK || workStatus == WorkStatus.EARLY_LEAVE) {
            payment = history.getWorkDate().getJobPost().getWage();
        }
        return PaymentMemberInfo.builder()
                .memberId(member.getId())
                .workerName(member.getWorkerInfo().getWorkerName())
                .age(AgeTransfer.getAgeByBirth(member.getWorkerInfo().getBrith()))
                .gender(member.getWorkerInfo().getGender())
                .payment(payment)
                .startStatus(history.getStartStatus())
                .endStatus(history.getEndStatus())
                .build();
    }
}
