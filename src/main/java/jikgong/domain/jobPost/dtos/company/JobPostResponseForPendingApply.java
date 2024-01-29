package jikgong.domain.jobPost.dtos.company;

import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.Tech;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class JobPostResponseForPendingApply {
    /**
     * 신청 내역 에서 보여질 JobPost 정보
     */
    private Long postId;
    private String title; // 공고 제목
    private Tech tech; // 기술

    public static JobPostResponseForPendingApply from(JobPost jobPost) {
        return JobPostResponseForPendingApply.builder()
                .postId(jobPost.getId())
                .title(jobPost.getTitle())
                .tech(jobPost.getTech())
                .build();
    }
}
