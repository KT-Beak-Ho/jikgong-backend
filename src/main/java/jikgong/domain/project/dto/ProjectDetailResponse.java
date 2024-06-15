package jikgong.domain.project.dto;

import jikgong.domain.jobpost.entity.JobPost;
import jikgong.domain.jobpost.entity.Park;
import jikgong.domain.jobpost.entity.Tech;
import jikgong.domain.member.entity.Member;
import jikgong.domain.project.entity.Project;
import jikgong.domain.workdate.entity.WorkDate;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ProjectDetailResponse {
    /**
     * 기업 검색 부분에서 사용
     */

    // 현장 정보
    private Long projectId;
    private String projectName; // 프로젝트 명
    private String companyName; // 기업 명  // todo: 원청사, 소속업체 가 기업명인지 질문

    // 건설 시공 정보
    private LocalDate startDate; // 착공일
    private LocalDate endDate; // 준공일

    private List<JobPostResponse> jobPostResponseList;

    public static ProjectDetailResponse from (Project project, List<WorkDate> workDateList) {
        List<JobPostResponse> jobPostResponseList = workDateList.stream()
                .map(JobPostResponse::from)
                .collect(Collectors.toList());

        return ProjectDetailResponse.builder()
                .projectId(project.getId())
                .projectName(project.getProjectName())
                .companyName(project.getMember().getCompanyInfo().getCompanyName())

                .startDate(project.getStartDate())
                .endDate(project.getEndDate())

                .jobPostResponseList(jobPostResponseList)
                .build();
    }


    @Getter
    @Builder
    public static class JobPostResponse {
        private Tech tech; // 직종
        private Integer wage; // 임금

        private LocalDate date; // 출역일
        private LocalTime startTime; // 시작 시간
        private LocalTime endTime; // 종료 시간

        private Integer recruitNum; // 모집 인원
        private Integer registeredNum; // 모집된 인원

        // 가능 여부
        private Boolean meal; // 식사 제공 여부
        private Boolean pickup; // 픽업 여부
        private Park park; // 주차 가능 여부

        // 기업 정보
        private String companyName;

        public static JobPostResponse from(WorkDate workDate) {
            JobPost jobPost = workDate.getJobPost();
            Member company = jobPost.getMember();

            return JobPostResponse.builder()
                    .tech(jobPost.getTech())
                    .wage(jobPost.getWage())
                    .date(workDate.getDate())
                    .startTime(jobPost.getStartTime())
                    .endTime(jobPost.getEndTime())
                    .recruitNum(workDate.getRecruitNum())
                    .registeredNum(workDate.getRegisteredNum())
                    .meal(jobPost.getAvailableInfo().getMeal())
                    .pickup(jobPost.getAvailableInfo().getPickup())
                    .park(jobPost.getAvailableInfo().getPark())
                    .companyName(company.getCompanyInfo().getCompanyName())
                    .build();
        }
    }
}
