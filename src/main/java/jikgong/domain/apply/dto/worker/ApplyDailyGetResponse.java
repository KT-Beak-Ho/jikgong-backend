package jikgong.domain.apply.dto.worker;

import java.time.LocalDateTime;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.jobpost.entity.jobpost.JobPost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplyDailyGetResponse {

    private Long applyId;
    private String address;
    private Integer wage;
    private ApplyProgress progress;

    @Getter
    @Builder
    public static class ApplyProgress {
        StatusDecisionTime lastProgressed;

        StatusDecisionTime created;
        StatusDecisionTime decided;
        StatusDecisionTime workStarted;
        StatusDecisionTime workEnded;
        StatusDecisionTime paid;

        @Getter
        @AllArgsConstructor
        public static class StatusDecisionTime {
            String status;
            LocalDateTime dateTime;
        }
    }

    public static ApplyDailyGetResponse from(Apply apply, ApplyProgress applyProgress) {
        JobPost jobPost = apply.getWorkDate().getJobPost();

        return ApplyDailyGetResponse.builder()
                .applyId(apply.getId())
                .address(jobPost.getJobPostAddress().getAddress())
                .wage(jobPost.getWage())
                .progress(applyProgress)
                .build();
    }
}
