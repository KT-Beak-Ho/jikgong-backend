package jikgong.domain.project.entity;

import jakarta.persistence.*;
import jikgong.domain.common.Address;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.member.entity.Member;
import jikgong.domain.project.dto.ProjectSaveRequest;
import jikgong.domain.project.dto.ProjectUpdateRequest;
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

    private String projectName; // 프로젝트 명
    private LocalDate startDate; // 착공일
    private LocalDate endDate; // 준공일
    @Embedded
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Project(String projectName, LocalDate startDate, LocalDate endDate, Address address, Member member) {
        this.projectName = projectName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.address = address;
        this.member = member;
    }

    public static Project createEntity(ProjectSaveRequest request, Member member) {
        return Project.builder()
            .projectName(request.getProjectName())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .address(new Address(request.getAddress(), request.getLatitude(), request.getLongitude()))
            .member(member)
            .build();
    }

    public void updateProject(ProjectUpdateRequest request) {
        this.projectName = request.getName();
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();
        this.address = new Address(request.getAddress(), request.getLatitude(), request.getLongitude());
    }
}
