package jikgong.domain.jobpost.dto.worker;

import jikgong.domain.jobpost.entity.JobPost;
import jikgong.domain.jobpost.entity.Tech;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JobPostMapResponse {

    private Long jobPostId;
    private Tech tech; // 직종
    private Float latitude; // 위도
    private Float longitude; // 경도

    public static JobPostMapResponse from(JobPost jobPost) {
        return JobPostMapResponse.builder()
            .jobPostId(jobPost.getId())
            .tech(jobPost.getTech())
            .latitude(jobPost.getJobPostAddress().getLatitude())
            .longitude(jobPost.getJobPostAddress().getLongitude())
            .build();
    }
}
