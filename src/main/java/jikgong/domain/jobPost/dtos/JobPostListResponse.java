package jikgong.domain.jobPost.dtos;

import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.Tech;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class JobPostListResponse {
    /**
     * 등록한 일자리 공고 목록 dto
     */
    private Long jobPostId;
    private String title;
    private LocalDateTime createdDate;
    private Integer applyNum;
    private Integer acceptedNum;
    // todo: 지원자, 확정자 수


    public static JobPostListResponse from(JobPost jobPost) {
        return JobPostListResponse.builder()
                .jobPostId(jobPost.getId())
                .title(jobPost.getTitle())
                .createdDate(jobPost.getCreatedDate())
                .build();
    }
}
