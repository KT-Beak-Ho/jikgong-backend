package jikgong.domain.project.dto;

import lombok.*;

@NoArgsConstructor
@Getter
@ToString
@Builder
@AllArgsConstructor
public class ProjectSaveResponse {
    Long projectId;
    ProjectDetailResponse project;
}
