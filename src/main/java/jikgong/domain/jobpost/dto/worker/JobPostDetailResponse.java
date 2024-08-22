package jikgong.domain.jobpost.dto.worker;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import jikgong.domain.jobpost.entity.JobPost;
import jikgong.domain.jobpost.entity.Park;
import jikgong.domain.jobpost.entity.Tech;
import jikgong.domain.jobpostimage.entity.JobPostImage;
import jikgong.domain.location.entity.Location;
import jikgong.domain.member.entity.Member;
import jikgong.domain.pickup.entity.Pickup;
import jikgong.domain.workdate.entity.WorkDate;
import jikgong.global.utils.DistanceCal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class JobPostDetailResponse {

    private Long jobPostId;
    private String title; // 제목
    private Tech tech; // 직종
    private LocalTime startTime; // 시작 시간
    private LocalTime endTime; // 종료 시간
    private Integer wage; // 임금

    private String workAddress; // 근무지
    private Double distance; // 근무지와의 거리

    private Boolean meal; // 식사 제공 여부
    private Boolean pickup; // 픽업 여부
    private List<String> pickupAddressList; // 픽업 장소 리스트
    private Park park; // 주차 가능 여부
    private String parkDetail; // 주차장 상세 설명
    private String preparation; // 준비 사항

    private List<WorkDateResponse> workDateResponseList; // 근무 날짜

    private String companyName; // 회사 명
    private String manager; // 담당자
    private String phone; // 연락처

    private String description; // 모집 공고 상세

    private List<String> imageUrls; // 이미지 정보

    public static JobPostDetailResponse from(JobPost jobPost, Location location) {
        Member member = jobPost.getMember();

        // 픽업 장소 리스트
        List<String> pickupAddressList = jobPost.getPickupList().stream()
            .map(Pickup::getAddress)
            .collect(Collectors.toList());

        // 근무 날짜
        List<WorkDateResponse> workDateResponseList = jobPost.getWorkDateList().stream()
            .map(WorkDateResponse::from)
            .sorted(Comparator.comparing(WorkDateResponse::getDate))
            .collect(Collectors.toList());

        // 이미지 리스트
        List<String> imageUrls = jobPost.getJobPostImageList().stream()
            .map(JobPostImage::getS3Url)
            .collect(Collectors.toList());

        return JobPostDetailResponse.builder()
            .jobPostId(jobPost.getId())
            .title(jobPost.getTitle())
            .tech(jobPost.getTech())
            .startTime(jobPost.getStartTime())
            .endTime(jobPost.getEndTime())
            .wage(jobPost.getWage())

            .workAddress(jobPost.getAddress().getAddress())
            .distance((location == null) ? null : DistanceCal.getDistance(jobPost, location))

            .meal(jobPost.getAvailableInfo().getMeal())
            .pickup(jobPost.getAvailableInfo().getPickup())
            .pickupAddressList(pickupAddressList)
            .park(jobPost.getAvailableInfo().getPark())
            .parkDetail(jobPost.getParkDetail())
            .preparation(jobPost.getPreparation())

            .workDateResponseList(workDateResponseList)

            .companyName(member.getCompanyInfo().getCompanyName())
            .manager(member.getCompanyInfo().getManager())
            .phone(member.getPhone())

            .description(jobPost.getDescription())

            .imageUrls(imageUrls)

            .build();
    }

    @Getter
    @Builder
    public static class WorkDateResponse {

        private Long workDateId;
        private LocalDate date;
        private Integer recruitNum; // 모집 인원
        private Integer registeredNum; // 모집된 인원

        public static WorkDateResponse from(WorkDate workDate) {
            return WorkDateResponse.builder()
                .workDateId(workDate.getId())
                .date(workDate.getDate())
                .recruitNum(workDate.getRecruitNum())
                .registeredNum(workDate.getRegisteredNum())
                .build();
        }
    }

}
