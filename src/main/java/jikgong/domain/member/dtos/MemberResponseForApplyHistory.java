package jikgong.domain.member.dtos;

import jikgong.domain.member.entity.Gender;
import jikgong.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class MemberResponseForApplyHistory {
    /**
     * 요청된 기록을 볼 때 노동자 정보를 담기 위한 클래스
     */
    private Long memberId;
    private String workerName; // 노동자 이름
    private Gender gender; // 성별
    private String nationality; // 국적

    public static MemberResponseForApplyHistory from(Member member) {
        return MemberResponseForApplyHistory.builder()
                .memberId(member.getId())
                .workerName(member.getWorkerInfo().getWorkerName())
                .gender(member.getWorkerInfo().getGender())
                .nationality(member.getWorkerInfo().getNationality())
                .build();
    }
}
