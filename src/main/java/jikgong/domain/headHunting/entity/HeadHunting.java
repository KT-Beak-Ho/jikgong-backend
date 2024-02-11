package jikgong.domain.headHunting.entity;

import jakarta.persistence.*;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.headHunting.dtos.HeadHuntingSaveRequest;
import jikgong.domain.jobPost.entity.AvailableInfo;
import jikgong.domain.member.entity.Member;
import jikgong.domain.skill.entity.Skill;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class HeadHunting extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "head_hunting_id")
    private Long id;

    private Integer career; // 경력
    @Embedded
    private AvailableInfo availableInfo; // 가능 여부 정보

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 양방향 매핑
    @OneToMany(mappedBy = "headHunting")
    private List<Skill> skillList = new ArrayList<>();

    @Builder
    public HeadHunting(Integer career, AvailableInfo availableInfo, Member member) {
        this.career = career;
        this.availableInfo = availableInfo;
        this.member = member;

        this.skillList = new ArrayList<>();
    }

    public static HeadHunting createEntity(HeadHuntingSaveRequest request, Member member) {
        return HeadHunting.builder()
                .career(request.getCareer())
                .availableInfo(new AvailableInfo(request.getMeal(), request.getPickup(), request.getPark()))
                .member(member)
                .build();
    }


}
