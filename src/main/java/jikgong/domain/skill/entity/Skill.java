package jikgong.domain.skill.entity;

import jakarta.persistence.*;
import jikgong.domain.jobPost.entity.Tech;
import lombok.AccessLevel;
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
}
