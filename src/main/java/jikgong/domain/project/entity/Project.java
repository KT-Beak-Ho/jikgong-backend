package jikgong.domain.project.entity;

import jakarta.persistence.*;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.member.entity.Member;
import jikgong.domain.project.dtos.ProjectSaveRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Project extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "project_id")
    private Long id;

    private String name; // 프로젝트 명
    private LocalDate startDate; // 착공일
    private LocalDate endDate; // 준공일
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Project(String name, LocalDate startDate, LocalDate endDate, String address, Member member) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.address = address;
        this.member = member;
    }

    public static Project createEntity(ProjectSaveRequest request, Member member) {
        return Project.builder()
                .name(request.getName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .address(request.getAddress())
                .member(member)
                .build();
    }
}
