package jikgong.domain.apply.dtos.worker;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.Tech;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@AllArgsConstructor
@Getter
@Builder
public class ApplyHistoryResponse {
    /**
     * 노동자의 요청 내역 조회(확정) 에 사용
     */
    private Long applyId;
    private ApplyStatus status;
    private LocalDateTime applyTime; // 신청 시간
    private LocalDateTime statusDecisionTime; // 신청 처리 시간

    private JobPostResponse jobPostResponse;

    public static ApplyHistoryResponse from(Apply apply) {
        return ApplyHistoryResponse.builder()
                .applyId(apply.getId())
                .status(apply.getStatus())
                .applyTime(apply.getCreatedDate())
                .statusDecisionTime(apply.getStatusDecisionTime())
                .jobPostResponse(JobPostResponse.from(apply.getWorkDate().getJobPost()))
                .build();
    }

    @AllArgsConstructor
    @Getter
    @Builder
    public static class JobPostResponse {
        /**
         * 신청 내역 에서 보여질 JobPost 정보
         */
        private Long postId;
        private String title; // 공고 제목
        private Tech tech; // 기술
        private Integer recruitNum; // 모집 인원
        private LocalTime startTime; // 시작 일시
        private Integer wage; // 임금
        private String address; // 도로명 주소

        public static JobPostResponse from(JobPost jobPost) {
            return JobPostResponse.builder()
                    .postId(jobPost.getId())
                    .title(jobPost.getTitle())
                    .tech(jobPost.getTech())
                    .recruitNum(jobPost.getRecruitNum())
                    .startTime(jobPost.getStartTime())
                    .wage(jobPost.getWage())
                    .address(jobPost.getAddress().getAddress())
                    .build();
        }
    }

}
