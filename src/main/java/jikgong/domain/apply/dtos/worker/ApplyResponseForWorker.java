package jikgong.domain.apply.dtos.worker;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.jobPost.dtos.company.JobPostResponseForAcceptedApply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class ApplyResponseForWorker {
    /**
     * 노동자의 요청 내역 조회(확정) 에 사용
     */
    private Long applyId;
    private ApplyStatus status;
    private LocalDateTime applyTime; // 신청 시간
    private LocalDateTime statusDecisionTime; // 신청 처리 시간

    private JobPostResponseForAcceptedApply jobPostResponse;

    public static ApplyResponseForWorker from(Apply apply) {
        return ApplyResponseForWorker.builder()
                .applyId(apply.getId())
                .status(apply.getStatus())
                .applyTime(apply.getCreatedDate())
                .statusDecisionTime(apply.getStatusDecisionTime())
                .jobPostResponse(JobPostResponseForAcceptedApply.from(apply.getWorkDate().getJobPost()))
                .build();
    }
}
