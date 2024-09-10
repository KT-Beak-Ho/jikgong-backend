package jikgong.domain.workexperience.dto;

import jikgong.domain.workexperience.entity.Tech;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkExperienceRequest {

    private Long workExperienceId;
    private Tech tech; // 직종
    private Integer experienceMonths; // 경력 기간 (월)
}
