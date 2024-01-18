package jikgong.domain.apply.dtos.company;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.member.dtos.MemberResponseForApplyHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ApplyPendingResponseForCompany {
    /**
     * 인력 관리: 대기 중인 요청 조회
     */
    private Long applyId;
    private MemberResponseForApplyHistory memberResponse;

    public static ApplyPendingResponseForCompany from(Apply apply) {
        return ApplyPendingResponseForCompany.builder()
                .applyId(apply.getId())
                .memberResponse(MemberResponseForApplyHistory.from(apply.getMember()))
                .build();
    }
}
