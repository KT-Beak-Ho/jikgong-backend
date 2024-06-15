package jikgong.domain.skill.dto;

import jikgong.domain.jobpost.entity.Tech;
import jikgong.domain.skill.entity.Skill;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SkillResponse {
    private Tech tech; // 직종
    private Integer skillPeriod; // 스킬 경력 기간 (월)

    public static SkillResponse from(Skill skill) {
        return SkillResponse.builder()
                .tech(skill.getTech())
                .skillPeriod(skill.getSkillPeriod())
                .build();
    }
}
