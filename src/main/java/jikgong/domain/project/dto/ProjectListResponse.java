package jikgong.domain.project.dto;

import jikgong.domain.project.entity.Project;
import jikgong.domain.project.entity.ProjectStatus;
import jikgong.domain.workdate.dto.JobStatisticsByProject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class ProjectListResponse {
    private Integer currentPage;
    private Integer totalPages;
    private Long totalElements;
    private Boolean hasNext;
    private List<ProjectSummary> content;

    public static ProjectListResponse from(Page metadata, List<ProjectSummary> projectSummaries) {
        return ProjectListResponse.builder()
                .currentPage(metadata.getNumber())
                .totalPages(metadata.getTotalPages())
                .totalElements(metadata.getTotalElements())
                .hasNext(metadata.hasNext())
                .content(projectSummaries)
                .build();
    }

    @Builder
    @Getter
    public static class ProjectSummary {
        private Long id;
        private String title; // 프로젝트 명
        private String location; // 위치
        private LocalDate startDate; // 착공일
        private LocalDate endDate; // 준공일
        private ProjectStatus status; // 상태
        private Integer progress;
        private Integer totalJobs;
        private Integer activeJobs;
        private Integer totalWorkers;
        private LocalDateTime createdAt;
        public static ProjectSummary from(Project project, JobStatisticsByProject job) {
            return ProjectSummary.builder()
                    .id(project.getId())
                    .title(project.getProjectName())
                    .location(project.getAddress().getAddress())
                    .startDate(project.getStartDate())
                    .endDate(project.getEndDate())
                    .status(project.calculateStatus())
                    .progress(project.calculateProgress())
                    .totalJobs(job.getTotalJobs())
                    .activeJobs(job.getActiveJobs())
                    .totalWorkers(job.getTotalWorkers())
                    .createdAt(project.getCreatedDate())
                    .build();
        }
    }
}
