package jikgong.domain.project.entity;

import jakarta.persistence.*;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Project extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "project_id")
    private Long id;

    private String name; // 프로젝트 명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Project(String name, Member member) {
        this.name = name;
        this.member = member;
    }
}
