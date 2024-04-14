package jikgong.domain.project.dtos;

import jikgong.domain.jobPost.dtos.project.JobPostListResponse;
import jikgong.domain.project.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
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

    private List<JobPostListResponse> jobPostListResponseList;

    public static ProjectDetailResponse from (Project project) {
        return ProjectDetailResponse.builder()
                .projectId(project.getId())
                .projectName(project.getProjectName())
                .companyName(project.getMember().getCompanyInfo().getCompanyName())

                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .build();
    }

    public void setJobPostListResponseList(List<JobPostListResponse> jobPostListResponseList) {
        this.jobPostListResponseList = jobPostListResponseList;
    }
}
