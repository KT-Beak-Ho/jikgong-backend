package jikgong.domain.history.dto;

import jikgong.domain.history.entity.History;
import jikgong.domain.history.entity.WorkStatus;
import jikgong.domain.member.entity.Gender;
import jikgong.domain.member.entity.Member;
import jikgong.global.utils.AgeTransfer;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentMemberInfo {
    private Integer payment; // 지급 금액
    private WorkStatus startStatus; // 출근, 결근
    private WorkStatus endStatus; // 퇴근, 조퇴

    private MemberResponse memberResponse; // 노동자 정보


    public static PaymentMemberInfo from(History history) {
        WorkStatus startStatus = history.getStartStatus();
        WorkStatus endStatus = history.getEndStatus();
        int payment = 0;

        // 출근 기록만 있는 경우 or 결근일 경우 => 0원
        if (startStatus == WorkStatus.START_WORK || startStatus == WorkStatus.NOT_WORK) {
            payment = 0;
        }
        // 퇴근, 조퇴일 경우 => 임금 지급
        if (endStatus == WorkStatus.FINISH_WORK || endStatus == WorkStatus.EARLY_LEAVE) {
            payment = history.getWorkDate().getJobPost().getWage();
        }
        return PaymentMemberInfo.builder()
                .payment(payment)
                .startStatus(startStatus)
                .endStatus(endStatus)
                .memberResponse(MemberResponse.from(history.getMember()))
                .build();
    }

    @Getter
    @Builder
    public static class MemberResponse {
        private Long memberId;
        private String workerName; // 노동자 명
        private Integer age; // 나이
        private Gender gender; // 성별

        private static MemberResponse from (Member member) {
            return MemberResponse.builder()
                    .memberId(member.getId())
                    .workerName(member.getWorkerInfo().getWorkerName())
                    .age(AgeTransfer.getAgeByBirth(member.getWorkerInfo().getBrith()))
                    .gender(member.getWorkerInfo().getGender())
                    .build();
        }
    }
}
