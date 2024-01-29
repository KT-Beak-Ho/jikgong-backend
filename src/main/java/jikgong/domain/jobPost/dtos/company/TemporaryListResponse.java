package jikgong.domain.jobPost.dtos.company;

import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.Tech;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class TemporaryListResponse {
    /**
     * 임시 저장한 일자리 공고 목록 dto
     */
    private Long jobPostId;
    private String title;
    private LocalDateTime lastModifiedDate;
    private Tech tech;
    private Integer recruitNum;
    private Integer wage;

    public static TemporaryListResponse from(JobPost jobPost) {
        return TemporaryListResponse.builder()
                .jobPostId(jobPost.getId())
                .title(jobPost.getTitle())
                .lastModifiedDate(jobPost.getLastModifiedDate())
                .tech(jobPost.getTech())
                .recruitNum(jobPost.getRecruitNum())
                .wage(jobPost.getWage())
                .build();
    }
}
