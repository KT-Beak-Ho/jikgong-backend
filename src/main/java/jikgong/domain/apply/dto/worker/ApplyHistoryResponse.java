package jikgong.domain.apply.dto.worker;

import java.time.LocalDate;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.jobpost.entity.jobpost.JobPost;
import jikgong.domain.workexperience.entity.Tech;
import jikgong.global.utils.TimeTransfer;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplyHistoryResponse {

    private Long applyId;
    private ApplyStatus status;
    private LocalDate applyDate; // 신청 시간
    private String timePassed; // 신청 시간
    private LocalDate workDate; // 지원 날짜
    private Integer applyNum; // 지원자 수

    private JobPostResponse jobPostResponse;

    public static ApplyHistoryResponse from(Apply apply) {
        return ApplyHistoryResponse.builder()
            .applyId(apply.getId())
            .status(apply.getStatus())
            .applyDate(apply.getCreatedDate().toLocalDate())
            .timePassed(TimeTransfer.getTimeDifference(apply.getCreatedDate()))
            .workDate(apply.getWorkDate().getDate())
            .applyNum(apply.getWorkDate().getApplyList().size())
            .jobPostResponse(JobPostResponse.from(apply.getWorkDate().getJobPost()))
            .build();
    }

    @Getter
    @Builder
    public static class JobPostResponse {

        private Long postId;
        private String title; // 공고 제목
        private Tech tech; // 기술

        public static JobPostResponse from(JobPost jobPost) {
            return JobPostResponse.builder()
                .postId(jobPost.getId())
                .title(jobPost.getTitle())
                .tech(jobPost.getTech())
                .build();
        }
    }
}
