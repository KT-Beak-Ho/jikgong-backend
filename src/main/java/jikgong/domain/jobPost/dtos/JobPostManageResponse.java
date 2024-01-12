package jikgong.domain.jobPost.dtos;

import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.workDate.entity.WorkDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class JobPostManageResponse {
    private Long jobPostId;
    private Tech tech;
    private Integer recruitNum;
    private LocalDate startDate; // 시작 날짜
    private LocalDate endDate; // 종료 날짜
    private LocalTime startTime; // 시작 시간
    private LocalTime endTime; // 종료 시간
    private Integer wage; // 임금
    private List<LocalDate> workDateList; // 일하는 날짜

    public static JobPostManageResponse from(JobPost jobPost) {
        List<LocalDate> workDateList = new ArrayList<>();
        for (WorkDate workDate : jobPost.getWorkDateList()) {
            workDateList.add(workDate.getWorkDate());
        }
        return JobPostManageResponse.builder()
                .jobPostId(jobPost.getId())
                .tech(jobPost.getTech())
                .recruitNum(jobPost.getRecruitNum())
                .startDate(jobPost.getStartDate())
                .endDate(jobPost.getEndDate())
                .startTime(jobPost.getStartTime())
                .endTime(jobPost.getEndTime())
                .wage(jobPost.getWage())
                .workDateList(workDateList)
                .build();

    }
}
