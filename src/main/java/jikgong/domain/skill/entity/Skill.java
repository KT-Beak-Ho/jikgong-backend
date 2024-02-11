package jikgong.domain.skill.entity;

import jakarta.persistence.*;
import jikgong.domain.headHunting.entity.HeadHunting;
import jikgong.domain.jobPost.entity.Tech;
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
    @JoinColumn(name = "head_hunting_id")
    private HeadHunting headHunting;

    @Builder
    public Skill(Tech tech, HeadHunting headHunting) {
        this.tech = tech;
        this.headHunting = headHunting;
    }

    public static Skill createEntity(Tech tech, HeadHunting headHunting) {
        return Skill.builder()
                .tech(tech)
                .headHunting(headHunting)
                .build();
    }
}
