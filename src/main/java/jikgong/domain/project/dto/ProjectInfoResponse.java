package jikgong.domain.project.dto;

import java.time.LocalDate;
import jikgong.domain.project.entity.Project;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectInfoResponse {

    /**
     * 프로젝트 수정 시 사용
     */
    private Long projectId;

    private String projectName; // 프로젝트 명
    private LocalDate startDate; // 착공일
    private LocalDate endDate; // 준공일

    private String address; // 주소
    private Float latitude; // 위도
    private Float longitude; // 경도

    public static ProjectInfoResponse from(Project project) {
        return ProjectInfoResponse.builder()
            .projectId(project.getId())
            .projectName(project.getProjectName())
            .startDate(project.getStartDate())
            .endDate(project.getEndDate())
            .address(project.getAddress().getAddress())
            .latitude(project.getAddress().getLatitude())
            .longitude(project.getAddress().getLongitude())
            .build();
    }
}
