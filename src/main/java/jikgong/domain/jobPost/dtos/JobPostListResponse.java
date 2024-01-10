package jikgong.domain.jobPost.dtos;

import jikgong.domain.jobPost.entity.JobPost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class JobPostListResponse {
    /**
     * 등록한 일자리 공고 목록 dto
     * 임시 저장한 일자리 공고 목록 dto
     */
    private Long jobPostId;
    private String title;
    private LocalDateTime lastModifiedDate;

    public static JobPostListResponse from(JobPost jobPost) {
        return JobPostListResponse.builder()
                .jobPostId(jobPost.getId())
                .title(jobPost.getTitle())
                .lastModifiedDate(jobPost.getLastModifiedDate())
                .build();
    }
}
