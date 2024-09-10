package jikgong.domain.workexperience.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jikgong.domain.member.entity.Member;
import jikgong.domain.workexperience.dto.WorkExperienceRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class WorkExperience {

    @Id
    @GeneratedValue
    @Column(name = "work_experience_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Tech tech;
    private Integer experienceMonths; // 스킬 경력 기간 (월)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public WorkExperience(Tech tech, Integer experienceMonths, Member member) {
        this.tech = tech;
        this.experienceMonths = experienceMonths;
        this.member = member;
    }

    public static WorkExperience from(WorkExperienceRequest request, Member member) {
        return WorkExperience.builder()
            .tech(request.getTech())
            .experienceMonths(request.getExperienceMonths())
            .member(member)
            .build();
    }

    public void updateExperienceMonths(WorkExperienceRequest request) {
        this.experienceMonths = request.getExperienceMonths();
    }
}
