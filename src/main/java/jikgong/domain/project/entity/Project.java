package jikgong.domain.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jikgong.domain.common.Address;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.member.entity.Member;
import jikgong.domain.project.dto.ProjectSaveRequest;
import jikgong.domain.project.dto.ProjectUpdateRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE project SET deleted_at = NOW() WHERE project_id = ?")
@Where(clause = "deleted_at IS NULL")
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

    private LocalDateTime deletedAt; // 논리 삭제

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
