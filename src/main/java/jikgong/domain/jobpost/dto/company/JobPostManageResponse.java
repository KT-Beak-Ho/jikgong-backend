package jikgong.domain.jobpost.dto.company;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import jikgong.domain.jobpost.entity.jobpost.JobPost;
import jikgong.domain.workdate.dto.WorkDateResponse;
import jikgong.domain.workexperience.entity.Tech;
import lombok.Builder;
import lombok.Getter;

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

        List<WorkDateResponse> workDateResponseList = jobPost.getWorkDateList().stream()
            .map(WorkDateResponse::from)
            .sorted(Comparator.comparing(WorkDateResponse::getDate))
            .collect(Collectors.toList());

        return JobPostManageResponse.builder()
            .jobPostId(jobPost.getId())
            .tech(jobPost.getTech())
            .recruitNum(jobPost.getRecruitNum())
            .startDate(jobPost.getStartDate())
            .endDate(jobPost.getEndDate())
            .startTime(jobPost.getStartTime())
            .endTime(jobPost.getEndTime())
            .wage(jobPost.getWage())
            .workDateResponseList(workDateResponseList)
            .build();
    }
}
