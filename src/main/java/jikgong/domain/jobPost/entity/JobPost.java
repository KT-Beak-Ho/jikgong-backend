package jikgong.domain.jobPost.entity;

import jakarta.persistence.*;
import jikgong.domain.common.Address;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.jobPost.dtos.JobPostSaveRequest;
import jikgong.domain.member.entity.Member;
import jikgong.domain.project.entity.Project;
import jikgong.domain.workDay.entity.WorkDay;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class JobPost extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "job_post_id")
    private Long id;

    private String title; // 제목
    @Enumerated(value = EnumType.STRING)
    private Tech tech;
    private LocalDate startDate; // 시작 날짜
    private LocalDate endDate; // 종료 날짜
    private LocalTime startTime; // 시작 시간
    private LocalTime endTime; // 종료 시간
    private Integer recruitNum; // 모집 인원
    private Integer registeredNum; // 모집된 인원
    private Integer wage; // 임금
    private String preparation; // 준비 사항
    private LocalDateTime expirationTime; // 모집 마감
    private Boolean isTemporary; // 임시 저장 여부

    @Embedded
    private AvailableInfo availableInfo; // 가능 여부 정보
    @Embedded
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    // 양방향 매핑
    @OneToMany(mappedBy = "jobPost")
    private List<WorkDay> workDayList = new ArrayList<>();

    @Builder
    public JobPost(String title, Tech tech, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, Integer recruitNum, Integer wage, String preparation, LocalDateTime expirationTime, Boolean isTemporary, AvailableInfo availableInfo, Address address, Member member, Project project) {
        this.title = title;
        this.tech = tech;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.recruitNum = recruitNum;
        this.registeredNum = 0;
        this.wage = wage;
        this.preparation = preparation;
        this.expirationTime = expirationTime;
        this.isTemporary = isTemporary;
        this.availableInfo = availableInfo;
        this.address = address;
        this.member = member;
        this.project = project;
    }

    public static JobPost createEntity(JobPostSaveRequest request, Member member, Project project) {
        // 가장 빠른 날짜 찾기
        LocalDate minDate = Collections.min(request.getWorkDayList());
        // 가장 느린 날짜 찾기
        LocalDate maxDate = Collections.max(request.getWorkDayList());

        return JobPost.builder()
                .title(request.getTitle())
                .tech(request.getTech())
                .startDate(minDate)
                .endDate(maxDate)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .recruitNum(request.getRecruitNum())
                .wage(request.getWage())
                .preparation(request.getPreparation())
                .expirationTime(request.getExpirationTime())
                .isTemporary(request.getIsTemporary())
                .availableInfo(new AvailableInfo(request.getMeal(), request.getPickup(), request.getPark()))
                .address(new Address(request.getAddress(), request.getLatitude(), request.getLongitude()))
                .member(member)
                .project(project)
                .build();
    }
}
