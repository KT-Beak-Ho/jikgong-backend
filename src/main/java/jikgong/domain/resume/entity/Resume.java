package jikgong.domain.resume.entity;

import jakarta.persistence.*;
import jikgong.domain.jobPost.entity.AvailableInfo;
import jikgong.domain.member.entity.Member;
import jikgong.domain.resume.dtos.ResumeSaveRequest;
import jikgong.domain.skill.entity.Skill;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Resume {
    @Id
    @GeneratedValue
    @Column(name = "resume_id")
    private Long id;

    private Integer career; // 경력
    private LocalTime preferTimeStart; // 시작 선호 시간
    private LocalTime preferTimeEnd; // 종료 선호 시간

    @Embedded
    private AvailableInfo availableInfo; // 가능 여부 정보

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 양방향 매핑
    @OneToMany(mappedBy = "resume")
    private List<Skill> skillList = new ArrayList<>();

    @Builder
    public Resume(Integer career, LocalTime preferTimeStart, LocalTime preferTimeEnd, AvailableInfo availableInfo, Member member) {
        this.career = career;
        this.preferTimeStart = preferTimeStart;
        this.preferTimeEnd = preferTimeEnd;
        this.availableInfo = availableInfo;
        this.member = member;

        this.skillList = new ArrayList<>();
    }

    public static Resume createEntity(ResumeSaveRequest request, Member member) {
        return Resume.builder()
                .career(request.getCareer())
                .preferTimeStart(request.getPreferTimeStart())
                .preferTimeEnd(request.getPreferTimeEnd())
                .availableInfo(new AvailableInfo(request.getMeal(), request.getPickup(), request.getPark()))
                .member(member)
                .build();
    }
}
