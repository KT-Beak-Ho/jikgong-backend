package jikgong.domain.apply.dtos.worker;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.jobPost.dtos.company.JobPostResponseForPendingApply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Builder
public class ApplyPendingResponse {
    private Long applyId;
    private ApplyStatus status;
    private LocalDate applyTime; // 신청 시간
    private LocalDate workDate; // 지원 날짜
    private Integer applyNum; // 지원자 수

    private JobPostResponseForPendingApply jobPostResponse;

    public static ApplyPendingResponse from(Apply apply) {
        return ApplyPendingResponse.builder()
                .applyId(apply.getId())
                .status(apply.getStatus())
                .applyTime(apply.getCreatedDate().toLocalDate())
                .workDate(apply.getWorkDate().getDate())
                .applyNum(apply.getWorkDate().getApplyList().size())
                .jobPostResponse(JobPostResponseForPendingApply.from(apply.getWorkDate().getJobPost()))
                .build();
    }
}
