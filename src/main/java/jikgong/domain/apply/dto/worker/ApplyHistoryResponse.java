package jikgong.domain.apply.dto.worker;

import java.time.LocalDate;
import java.time.LocalTime;

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
    private ApplyEffectiveStatus status;
    private LocalDate applyDate; // 신청 시간
    private String timePassed; // 신청 시간
    private LocalDate workDate; // 지원 날짜
    private Integer applyNum; // 지원자 수

    private JobPostResponse jobPostResponse;

    @Getter
    public enum ApplyEffectiveStatus {
        PROCESSING("진행중"),
        CLOSED("마감"),
        FINISHED("완료");

        private final String description;

        ApplyEffectiveStatus(String description) {
            this.description = description;
        }
    }

    public static ApplyHistoryResponse from(Apply apply) {
        ApplyEffectiveStatus effectiveStatus = determineEffectiveStatus(apply.getStatus());

        return ApplyHistoryResponse.builder()
            .applyId(apply.getId())
            .status(effectiveStatus)
            .applyDate(apply.getCreatedDate().toLocalDate())
            .timePassed(TimeTransfer.getTimeDifference(apply.getCreatedDate()))
            .workDate(apply.getWorkDate().getDate())
            .applyNum(apply.getWorkDate().getApplyList().size())
            .jobPostResponse(JobPostResponse.from(apply.getWorkDate().getJobPost()))
            .build();
    }

    private static ApplyEffectiveStatus determineEffectiveStatus(ApplyStatus originalStatus) {
        if (originalStatus == ApplyStatus.ACCEPTED) {
            return ApplyEffectiveStatus.FINISHED;
        } else if (originalStatus == ApplyStatus.PENDING || originalStatus == ApplyStatus.OFFERED) {
            return ApplyEffectiveStatus.PROCESSING;
        } else {
            // REJECTED, CANCELED 등 나머지 상태는 모두 "마감"으로 처리
            return ApplyEffectiveStatus.CLOSED;
        }
    }

    @Getter
    @Builder
    public static class JobPostResponse {

        private Long postId;
        private String title; // 공고 제목
        private Tech tech; // 기술
        private LocalDate endDate; // 종료 날짜
        private LocalTime endTime; // 종료 시간

        public static JobPostResponse from(JobPost jobPost) {
            return JobPostResponse.builder()
                .postId(jobPost.getId())
                .title(jobPost.getTitle())
                .tech(jobPost.getTech())
                .endDate(jobPost.getEndDate())
                .endTime(jobPost.getEndTime())
                .build();
        }
    }
}
