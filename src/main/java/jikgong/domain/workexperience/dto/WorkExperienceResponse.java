package jikgong.domain.workexperience.dto;

import jikgong.domain.workexperience.entity.Tech;
import jikgong.domain.workexperience.entity.WorkExperience;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkExperienceResponse {

    private Long workExperienceId;
    private String tech; // 직종
    private Integer experienceMonths; // 경력 기간 (월)

    public static WorkExperienceResponse from(WorkExperience workExperience) {
        return WorkExperienceResponse.builder()
            .workExperienceId(workExperience.getId())
            .tech(workExperience.getTech().getDescription())
            .experienceMonths(workExperience.getExperienceMonths())
            .build();
    }
}
