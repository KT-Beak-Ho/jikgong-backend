package jikgong.domain.apply.dto.worker;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.entity.ApplyStatus;
import jikgong.domain.history.entity.History;
import jikgong.domain.history.entity.WorkStatus;
import jikgong.domain.jobpost.entity.jobpost.JobPost;
import jikgong.domain.workexperience.entity.Tech;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplyAcceptedGetResponse {

    /**
     * 노동자의 요청 내역 조회(확정) 에 사용
     */
    private Long applyId;
    private ApplyStatus status;
    private Boolean isOffer; // Offer로 인해 생성된 Apply인지 여부
    private LocalDateTime applyTime; // 신청 시간
    private LocalDateTime statusDecisionTime; // 신청 처리 시간

    private JobPostResponse jobPostResponse;
    private HistoryResponse historyResponse;

    public static ApplyAcceptedGetResponse from(Apply apply, Optional<History> history) {
        // 출,퇴근 정보
        HistoryResponse historyResponse = null;
        if (history.isPresent()) {
            historyResponse = HistoryResponse.from(history.get());
        }

        return ApplyAcceptedGetResponse.builder()
            .applyId(apply.getId())
            .status(apply.getStatus())
            .isOffer(apply.getIsOffer())
            .applyTime(apply.getCreatedDate())
            .statusDecisionTime(apply.getStatusDecisionTime())
            .jobPostResponse(JobPostResponse.from(apply.getWorkDate().getJobPost()))
            .historyResponse(historyResponse)
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
                .address(jobPost.getJobPostAddress().getAddress())
                .build();
        }
    }

    @Getter
    @Builder
    public static class HistoryResponse {

        private Long historyId;
        private WorkStatus startStatus; // 출근, 결근
        private LocalDateTime startStatusDecisionTime; // 출근, 결근 처리 시간
        private WorkStatus endStatus; // 퇴근, 조퇴
        private LocalDateTime endStatusDecisionTime; // 퇴근, 조퇴 처리 시간

        public static HistoryResponse from(History history) {
            return HistoryResponse.builder()
                .historyId(history.getId())
                .startStatus(history.getStartStatus())
                .startStatusDecisionTime(history.getStartStatusDecisionTime())
                .endStatus(history.getEndStatus())
                .endStatusDecisionTime(history.getEndStatusDecisionTime())
                .build();
        }
    }
}
