package jikgong.domain.project.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jikgong.domain.common.Address;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.member.entity.Member;
import jikgong.domain.project.dto.ProjectSaveRequest;
import jikgong.domain.project.dto.ProjectUpdateRequest;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
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
    private String description; // 프로젝트 설명
    @Embedded
    private Address address;
    @Enumerated
    private ProjectStatus status;

    private LocalDateTime deletedAt; // 논리 삭제

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Project(String projectName, LocalDate startDate, LocalDate endDate, Address address, String description, Member member) {
        this.projectName = projectName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.address = address;
        this.description = description;
        this.member = member;
        LocalDate now = LocalDate.now();
        if(now.isBefore(startDate)) {
            this.status = ProjectStatus.PLANNED;
        } else if(now.isAfter(endDate)){
            throw new JikgongException(ErrorCode.PROJECT_CREATE_FAIL);
        } else {
            this.status = ProjectStatus.IN_PROGRESS;
        }
    }

    public static Project createEntity(ProjectSaveRequest request, Member member) {
        return Project.builder()
            .projectName(request.getProjectName())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .address(request.getLocation())
            .description(request.getDescription())
            .member(member)
            .build();
    }

    public void updateProject(ProjectUpdateRequest request) {
        this.projectName = request.getTitle();
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();
        this.address = request.getLocation();
        this.description = request.getDescription();
    }

    public void updateProjectStatus(Project project) {

    }

    public ProjectStatus calculateStatus() {
        LocalDate today = LocalDate.now();
        if (today.isBefore(this.startDate)) return ProjectStatus.PLANNED;
        if (today.isAfter(this.endDate)) return  ProjectStatus.COMPLETED;
        return ProjectStatus.IN_PROGRESS;
    }

    public Integer calculateProgress() {
        LocalDate today = LocalDate.now();
        if (today.isBefore(this.startDate)) return 0;
        if (today.isAfter(this.endDate)) return 100;

        long totalDuration = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long elapsedDuration = ChronoUnit.DAYS.between(startDate, today) + 1;
        return (int) (((double) elapsedDuration/totalDuration) * 100);
    }
}
