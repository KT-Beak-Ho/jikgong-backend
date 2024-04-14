package jikgong.domain.apply.dtos.company;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.member.dtos.apply.MemberResponseForApplyHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Builder
@ToString
public class ApplyResponseForCompany {
    /**
     * 인력 관리: 대기 중인 인부 조회
     * 인력 관리: 확정 된 인부 조회
     */
    private Long applyId;
    private MemberResponseForApplyHistory memberResponse;

    public static ApplyResponseForCompany from(Apply apply) {
        return ApplyResponseForCompany.builder()
                .applyId(apply.getId())
                .memberResponse(MemberResponseForApplyHistory.from(apply.getMember()))
                .build();
    }
}
