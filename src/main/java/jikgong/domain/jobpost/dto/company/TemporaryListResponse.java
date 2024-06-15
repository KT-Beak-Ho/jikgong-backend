package jikgong.domain.jobpost.dto.company;

import jikgong.domain.jobpost.entity.JobPost;
import jikgong.domain.jobpost.entity.Tech;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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
