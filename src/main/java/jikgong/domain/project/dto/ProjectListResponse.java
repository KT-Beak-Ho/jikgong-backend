package jikgong.domain.project.dto;

import jikgong.domain.project.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Builder
public class ProjectListResponse {
    private Long projectId;
    private String projectName; // 프로젝트 명
    private LocalDate startDate; // 착공일
    private LocalDate endDate; // 준공일

    public static ProjectListResponse from(Project project) {
        return ProjectListResponse.builder()
                .projectId(project.getId())
                .projectName(project.getProjectName())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .build();
    }
}
