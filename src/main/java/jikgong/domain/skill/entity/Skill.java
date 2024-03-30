package jikgong.domain.skill.entity;

import jakarta.persistence.*;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.resume.dtos.CareerDetailRequest;
import jikgong.domain.resume.entity.Resume;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Skill {
    @Id
    @GeneratedValue
    @Column(name = "skill_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Tech tech;
    private Integer skillPeriod; // 스킬 경력 기간 (월)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @Builder
    public Skill(Tech tech, Integer skillPeriod, Resume resume) {
        this.tech = tech;
        this.skillPeriod = skillPeriod;
        this.resume = resume;
    }

    public static Skill createEntity(CareerDetailRequest request, Resume resume) {
        return Skill.builder()
                .tech(request.getTech())
                .skillPeriod(request.getSkillPeriod())
                .resume(resume)
                .build();
    }
}
