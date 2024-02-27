package jikgong.domain.jobPost.dtos.headhunting;

import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.workDate.dtos.WorkDateResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class JobPostListResponse {
    private Long jobPostId;
    private Tech tech; // 타입
    private String title; // 모집 공고 제목
    private List<WorkDateResponse> availableDate; // 제안 가능 날짜
    private LocalTime startTime; // 시작 시간
    private LocalTime endTime; // 종료 시간
    private Integer wage; // 임금

    public static JobPostListResponse from(JobPost jobPost, List<WorkDateResponse> availableDate) {
        return JobPostListResponse.builder()
                .jobPostId(jobPost.getId())
                .tech(jobPost.getTech())
                .title(jobPost.getTitle())
                .availableDate(availableDate)
                .startTime(jobPost.getStartTime())
                .endTime(jobPost.getEndTime())
                .wage(jobPost.getWage())
                .build();
    }
}
