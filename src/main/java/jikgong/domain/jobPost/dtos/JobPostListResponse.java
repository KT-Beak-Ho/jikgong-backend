package jikgong.domain.jobPost.dtos;

import jikgong.domain.jobPost.entity.JobPost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class JobPostListResponse {
    /**
     * 등록한 일자리 공고 목록 dto
     * 임시 저장한 일자리 공고 목록 dto
     */
    private Long jobPostId;
    private String title;

    public static JobPostListResponse from(JobPost jobPost) {
        return JobPostListResponse.builder()
                .jobPostId(jobPost.getId())
                .title(jobPost.getTitle())
                .build();
    }
}
