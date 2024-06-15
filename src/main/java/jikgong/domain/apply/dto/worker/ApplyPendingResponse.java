package jikgong.domain.apply.dto.worker;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.jobpost.entity.JobPost;
import jikgong.domain.jobpost.entity.Tech;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ApplyPendingResponse {
    private Long applyId;
    private ApplyStatus status;
    private LocalDate applyTime; // 신청 시간
    private LocalDate workDate; // 지원 날짜
    private Integer applyNum; // 지원자 수

    private JobPostResponse jobPostResponse;

    public static ApplyPendingResponse from(Apply apply) {
        return ApplyPendingResponse.builder()
                .applyId(apply.getId())
                .status(apply.getStatus())
                .applyTime(apply.getCreatedDate().toLocalDate())
                .workDate(apply.getWorkDate().getDate())
                .applyNum(apply.getWorkDate().getApplyList().size())
                .jobPostResponse(JobPostResponse.from(apply.getWorkDate().getJobPost()))
                .build();
    }

    @Getter
    @Builder
    public static class JobPostResponse {
        /**
         * 신청 내역 에서 보여질 JobPost 정보
         */
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
