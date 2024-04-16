package jikgong.domain.history.dtos;

import jikgong.domain.history.entity.History;
import jikgong.domain.history.entity.WorkStatus;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.member.entity.Gender;
import jikgong.domain.member.entity.Member;
import jikgong.global.utils.AgeTransfer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class HistoryManageResponse {
    /**
     * 인력 관리 화면에서 출퇴근 관련 dto
     */
    private Long historyId;
    private WorkStatus startStatus; // 출근,결근 여부
    private WorkStatus endStatus; // 퇴근, 조퇴 여부

    private MemberResponse memberResponse; // 노동자 정보

    public static HistoryManageResponse from(Apply apply) {
        Member member = apply.getMember();

        return HistoryManageResponse.builder()
                .memberResponse(MemberResponse.from(member))
                .build();
    }

    public static HistoryManageResponse from(History history) {
        Member member = history.getMember();

        return HistoryManageResponse.builder()
                .historyId(history.getId())
                .startStatus(history.getStartStatus())
                .endStatus(history.getEndStatus())
                .memberResponse(MemberResponse.from(member))
                .build();
    }

    public void updateHistoryInfo(History history) {
        this.historyId = history.getId();
        this.startStatus = history.getStartStatus();
        this.endStatus = history.getEndStatus();
    }

    @Builder
    @Getter
    public static class MemberResponse {
        private Long memberId;
        private String workerName; // 노동자 이름
        private String phone; // 휴대폰 번호
        private Integer age; // 나이
        private Gender gender; // 성별

        public static MemberResponse from(Member member) {
            return MemberResponse.builder()
                    .memberId(member.getId())
                    .workerName(member.getWorkerInfo().getWorkerName())
                    .phone(member.getPhone())
                    .age(AgeTransfer.getAgeByBirth(member.getWorkerInfo().getBrith()))
                    .gender(member.getWorkerInfo().getGender())
                    .build();
        }
    }
}
