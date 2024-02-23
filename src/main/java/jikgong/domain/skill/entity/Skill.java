package jikgong.domain.skill.entity;

import jakarta.persistence.*;
import jikgong.domain.headHunting.entity.HeadHunting;
import jikgong.domain.jobPost.entity.Tech;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @Builder
    public Skill(Tech tech, Resume resume) {
        this.tech = tech;
        this.resume = resume;
    }

    public static Skill createEntity(Tech tech, Resume resume) {
        return Skill.builder()
                .tech(tech)
                .resume(resume)
                .build();
    }
}
