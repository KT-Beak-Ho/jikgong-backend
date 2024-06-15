package jikgong.domain.jobpost.dto.company;

import jikgong.domain.jobpost.entity.JobPost;
import jikgong.domain.jobpost.entity.Tech;
import jikgong.domain.workdate.dto.WorkDateResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class JobPostManageResponse {

    private Long jobPostId;
    private Tech tech; // 인부 타입
    private Integer recruitNum;
    private LocalDate startDate; // 시작 날짜
    private LocalDate endDate; // 종료 날짜
    private LocalTime startTime; // 시작 시간
    private LocalTime endTime; // 종료 시간
    private Integer wage; // 임금
    private List<WorkDateResponse> workDateResponseList; // 일하는 날짜

    public static JobPostManageResponse from(JobPost jobPost) {
        return JobPostManageResponse.builder()
            .jobPostId(jobPost.getId())
            .tech(jobPost.getTech())
            .recruitNum(jobPost.getRecruitNum())
            .startDate(jobPost.getStartDate())
            .endDate(jobPost.getEndDate())
            .startTime(jobPost.getStartTime())
            .endTime(jobPost.getEndTime())
            .wage(jobPost.getWage())
            .workDateResponseList(
                jobPost.getWorkDateList().stream().map(WorkDateResponse::from).collect(Collectors.toList()))
            .build();
    }
}
