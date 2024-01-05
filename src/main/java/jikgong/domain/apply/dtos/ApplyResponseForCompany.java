package jikgong.domain.apply.dtos;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.member.dtos.MemberResponseForApplyHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ApplyResponseForCompany {
    /**
     * 공고에 해당하는 요청 조회에 사용 (기업)
     */
    private Long applyId;
    private ApplyStatus status;
    private MemberResponseForApplyHistory memberResponse;

    public static ApplyResponseForCompany from(Apply apply) {
        return ApplyResponseForCompany.builder()
                .applyId(apply.getId())
                .status(apply.getStatus())
                .memberResponse(MemberResponseForApplyHistory.from(apply.getMember()))
                .build();
    }
}
