package jikgong.domain.jobpost.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jikgong.domain.common.BaseEntity;
import jikgong.domain.jobpost.dto.company.JobPostSaveRequest;
import jikgong.domain.jobpost.dto.company.TemporarySaveRequest;
import jikgong.domain.jobpostimage.entity.JobPostImage;
import jikgong.domain.member.entity.Member;
import jikgong.domain.pickup.entity.Pickup;
import jikgong.domain.project.entity.Project;
import jikgong.domain.scrap.entity.Scrap;
import jikgong.domain.workdate.entity.WorkDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE job_post SET deleted_at = NOW() WHERE job_post_id = ?")
@Where(clause = "deleted_at IS NULL")
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
    @Column(columnDefinition = "TEXT")
    private String description; // 모집 공고 설명

    private Boolean isTemporary; // 임시 저장 여부
    private LocalDateTime deletedAt; // 논리 삭제

    @Embedded
    private AvailableInfo availableInfo; // 가능 여부 정보
    @Embedded
    private JobPostAddress jobPostAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    // 양방향 매핑
    @OneToMany(mappedBy = "jobPost")
    private List<WorkDate> workDateList = new ArrayList<>();
    @OneToMany(mappedBy = "jobPost")
    private List<Pickup> pickupList = new ArrayList<>();
    @OneToMany(mappedBy = "jobPost")
    private List<Scrap> scrapList = new ArrayList<>();
    @OneToMany(mappedBy = "jobPost")
    private List<JobPostImage> jobPostImageList = new ArrayList<>();

    @Builder
    public JobPost(String title, Tech tech, LocalDate startDate, LocalDate endDate, LocalTime startTime,
        LocalTime endTime, Integer recruitNum, Integer wage, String parkDetail, String preparation, String managerName,
        String phone, String description, Boolean isTemporary, AvailableInfo availableInfo,
        JobPostAddress jobPostAddress,
        Member member,
        Project project) {
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
        this.description = description;
        this.isTemporary = isTemporary;
        this.availableInfo = availableInfo;
        this.jobPostAddress = jobPostAddress;
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

        // 모집 공고 위치 정보
        JobPostAddress jobPostAddress = JobPostAddress.builder()
            .address(request.getAddress())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .city(request.getCity())
            .district(request.getDistrict())
            .build();

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
            .description(request.getDescription())
            .isTemporary(true)
            .availableInfo(new AvailableInfo(request.getMeal(), request.getPickup(), request.getPark()))
            .jobPostAddress(jobPostAddress)
            .member(member)
            .project(project)
            .build();
    }

    public static JobPost createEntityByJobPost(JobPostSaveRequest request, Member member, Project project) {
        // 가장 빠른 날짜 찾기
        LocalDate minDate = Collections.min(request.getDateList());
        // 가장 느린 날짜 찾기
        LocalDate maxDate = Collections.max(request.getDateList());

        // 모집 공고 위치 정보
        JobPostAddress jobPostAddress = JobPostAddress.builder()
            .address(request.getAddress())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .city(request.getCity())
            .district(request.getDistrict())
            .build();

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
            .description(request.getDescription())
            .isTemporary(false)
            .availableInfo(new AvailableInfo(request.getMeal(), request.getPickup(), request.getPark()))
            .jobPostAddress(jobPostAddress)
            .member(member)
            .project(project)
            .build();
    }
}
