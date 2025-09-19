package jikgong.domain.project.dto;

import jikgong.domain.common.Address;
import jikgong.domain.project.entity.Project;
import jikgong.domain.project.entity.ProjectStatus;
import jikgong.domain.workdate.dto.JobStatisticsByProject;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ProjectDetailResponse {
    Long id;
    String title;
    Address locationData;
    LocalDate startDate;
    LocalDate endDate;
    ProjectStatus status;
    Integer progress;
    String description;
    // List<JobSummary> jobs;
    ProjectStatistics statistics;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    @Getter
    @Builder
    public static class ProjectStatistics {
        private Integer totalJobs;
        private Integer completedJobs;
        private Integer totalWorkers;
        private Long totalPayments;
        private Long savedFees;
    }

    public static ProjectDetailResponse from(Project project, JobStatisticsByProject job) {
        return ProjectDetailResponse.builder()
                .id(project.getId())
                .title(project.getProjectName())
                .locationData(project.getAddress())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .status(project.calculateStatus())
                .progress(project.calculateProgress())
                .description(project.getDescription())
                .statistics(ProjectStatistics.builder()
                        .totalJobs(job.getTotalJobs())
                        .completedJobs(1234567890)
                        .totalWorkers(job.getTotalWorkers())
                        .totalPayments(1234567890L)
                        .savedFees(1234567890L)
                        .build())
                .createdAt(project.getCreatedDate())
                .updatedAt(project.getLastModifiedDate())
                .build();
    }

}
