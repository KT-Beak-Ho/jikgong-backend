package jikgong.domain.jobPost.entity;

import jakarta.persistence.*;
import jikgong.domain.pickup.entity.Pickup;
import jikgong.domain.common.Address;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.jobPost.dtos.company.JobPostSaveRequest;
import jikgong.domain.jobPost.dtos.company.TemporarySaveRequest;
import jikgong.domain.jobPostImage.entity.JobPostImage;
import jikgong.domain.member.entity.Member;
import jikgong.domain.project.entity.Project;
import jikgong.domain.scrap.entity.Scrap;
import jikgong.domain.workDate.entity.WorkDate;
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
    @Id
    @GeneratedValue
    @Column(name = "job_post_id")
    private Long id;

    private String title; // 제목
    @Enumerated(value = EnumType.STRING)
    private Tech tech; // 직종
    private LocalDate startDate; // 시작 날짜
    private LocalDate endDate; // 종료 날짜
    private LocalTime startTime; // 시작 시간
    private LocalTime endTime; // 종료 시간
    private Integer recruitNum; // 모집 인원
    private Integer wage; // 임금
    private String parkDetail; // 주차장 상세 설명
    private String preparation; // 준비 사항
    private String managerName; // 담당자 명
    private String phone; // 연락 번호
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
    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkDate> workDateList = new ArrayList<>();
    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pickup> pickupList = new ArrayList<>();
    @OneToMany(mappedBy = "jobPost")
    private List<Scrap> scrapList = new ArrayList<>();
    @OneToMany(mappedBy = "jobPost")
    private List<JobPostImage> jobPostImageList = new ArrayList<>();

    @Builder
    public JobPost(String title, Tech tech, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, Integer recruitNum, Integer wage, String parkDetail, String preparation, LocalDateTime expirationTime, String managerName, String phone, Boolean isTemporary, AvailableInfo availableInfo, Address address, Member member, Project project) {
        this.title = title;
        this.tech = tech;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.parkDetail = parkDetail;
        this.recruitNum = recruitNum;
        this.wage = wage;
        this.preparation = preparation;
        this.managerName = managerName;
        this.phone = phone;
        this.isTemporary = isTemporary;
        this.availableInfo = availableInfo;
        this.address = address;
        this.member = member;
        this.project = project;

        this.workDateList = new ArrayList<>();
        this.pickupList = new ArrayList<>();
        this.scrapList = new ArrayList<>();
        this.jobPostImageList = new ArrayList<>();
    }

    public static JobPost createEntityByTemporary(TemporarySaveRequest request, Member member, Project project) {
        LocalDate minDate = null;
        LocalDate maxDate = null;
        if (request.getWorkDateList() != null) {
            // 가장 빠른 날짜 찾기
            minDate = Collections.min(request.getWorkDateList());
            // 가장 느린 날짜 찾기
            maxDate = Collections.max(request.getWorkDateList());
        }
        return JobPost.builder()
                .title(request.getTitle())
                .tech(request.getTech())
                .startDate(minDate)
                .endDate(maxDate)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .recruitNum(request.getRecruitNum())
                .wage(request.getWage())
                .parkDetail(request.getParkDetail())
                .preparation(request.getPreparation())
                .managerName(request.getManagerName())
                .phone(request.getPhone())
                .isTemporary(true)
                .availableInfo(new AvailableInfo(request.getMeal(), request.getPickup(), request.getPark()))
                .address(new Address(request.getAddress(), request.getLatitude(), request.getLongitude()))
                .member(member)
                .project(project)
                .build();
    }

    public static JobPost createEntityByJobPost(JobPostSaveRequest request, Member member, Project project) {
        // 가장 빠른 날짜 찾기
        LocalDate minDate = Collections.min(request.getWorkDateList());
        // 가장 느린 날짜 찾기
        LocalDate maxDate = Collections.max(request.getWorkDateList());
        return JobPost.builder()
                .title(request.getTitle())
                .tech(request.getTech())
                .startDate(minDate)
                .endDate(maxDate)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .recruitNum(request.getRecruitNum())
                .wage(request.getWage())
                .parkDetail(request.getParkDetail())
                .preparation(request.getPreparation())
                .managerName(request.getManagerName())
                .phone(request.getPhone())
                .isTemporary(false)
                .availableInfo(new AvailableInfo(request.getMeal(), request.getPickup(), request.getPark()))
                .address(new Address(request.getAddress(), request.getLatitude(), request.getLongitude()))
                .member(member)
                .project(project)
                .build();
    }

    public void deleteChildEntity(JobPost jobPost) {
        // 근무 날짜 정보 삭제
        jobPost.getWorkDateList().clear();
        // 위치 관련 정보 삭제
        jobPost.getPickupList().clear();
    }
}
