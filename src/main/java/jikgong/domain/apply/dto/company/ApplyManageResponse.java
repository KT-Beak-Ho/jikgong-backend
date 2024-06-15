package jikgong.domain.apply.dto.company;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.history.entity.History;
import jikgong.domain.history.entity.WorkStatus;
import jikgong.domain.member.entity.Gender;
import jikgong.domain.member.entity.Member;
import jikgong.global.utils.AgeTransfer;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ApplyManageResponse {
    /**
     * 인력 관리: 대기 중인 인부 조회
     * 인력 관리: 확정 된 인부 조회
     */
    private Long applyId;
    private MemberResponse memberResponse;

    public static ApplyManageResponse from(Apply apply) {
        return ApplyManageResponse.builder()
                .applyId(apply.getId())
                .memberResponse(MemberResponse.from(apply.getMember()))
                .build();
    }

    @Getter
    @Builder
    public static class MemberResponse {
        private Long memberId;
        private String workerName; // 노동자 이름
        private String phone; // 휴대폰 번호
        private Integer age; // 나이
        private Gender gender; // 성별
        private String nationality; // 국적
        private Integer workTimes; // 출역 횟수
        private Float participationRate; // 참여율

        public static MemberResponse from(Member member) {
            // 나이 계산
            int age = AgeTransfer.getAgeByBirth(member.getWorkerInfo().getBrith());

            // 출역 횟수, 참여율
            List<History> workHistory = member.getHistoryList().stream()
                    .filter(history -> history.getEndStatus() == WorkStatus.FINISH_WORK)
                    .collect(Collectors.toList());
            int workTimes = workHistory.size();
            float participationRate = (float) workTimes / (float) member.getHistoryList().size();

            // 출역 내역이 없을 경우 -1
            if (workTimes == 0) {
                participationRate = -1;
            }

            return MemberResponse.builder()
                    .memberId(member.getId())
                    .workerName(member.getWorkerInfo().getWorkerName())
                    .phone(member.getPhone())
                    .age(age)
                    .gender(member.getWorkerInfo().getGender())
                    .nationality(member.getWorkerInfo().getNationality())
                    .workTimes(workTimes)
                    .participationRate(participationRate)
                    .build();
        }
    }
}
