package jikgong.domain.apply.dtos;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.Status;
import jikgong.domain.jobPost.dtos.JobPostApplyHistoryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ApplyResponseForWorker {
    /**
     * 노동자의 요청 내역 조회에 사용
     */
    private Long applyId;
    private Status status;
    private JobPostApplyHistoryResponse jobPostResponse;

    public static ApplyResponseForWorker from(Apply apply) {
        return ApplyResponseForWorker.builder()
                .applyId(apply.getId())
                .status(apply.getStatus())
                .jobPostResponse(JobPostApplyHistoryResponse.from(apply.getJobPost()))
                .build();
    }
}
