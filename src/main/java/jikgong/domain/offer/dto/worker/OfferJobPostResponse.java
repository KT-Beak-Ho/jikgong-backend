package jikgong.domain.offer.dto.worker;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.jobpost.entity.JobPost;
import jikgong.domain.jobpost.entity.Park;
import jikgong.domain.jobpost.entity.Tech;
import jikgong.domain.jobpostimage.entity.JobPostImage;
import jikgong.domain.location.entity.Location;
import jikgong.domain.member.entity.Member;
import jikgong.domain.offerworkdate.entity.OfferWorkDate;
import jikgong.domain.pickup.entity.Pickup;
import jikgong.domain.workdate.dto.WorkDateResponse;
import jikgong.domain.workdate.entity.WorkDate;
import jikgong.global.utils.DistanceCal;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class OfferJobPostResponse {

    private Long offerWorkDateId;

    private JobPostResponse jobPostResponse; // 모집 공고 정보
    private CompanyResponse companyResponse; // 기업 정보
    private WorkDateResponse workDateResponse; // 출역 제안 날짜

    public static OfferJobPostResponse from(OfferWorkDate offerWorkDate, List<Apply> acceptedApply, Location location) {
        WorkDate workDate = offerWorkDate.getWorkDate();
        JobPost jobPost = workDate.getJobPost();
        Member company = jobPost.getMember();

        return OfferJobPostResponse.builder()
            .offerWorkDateId(offerWorkDate.getId())
            .workDateResponse(WorkDateResponse.from(workDate))
            .jobPostResponse(JobPostResponse.from(offerWorkDate, acceptedApply, location)) // 모집 공고 정보
            .companyResponse(CompanyResponse.from(company))
            .build();
    }

    @Getter
    @Builder
    public static class CompanyResponse {

        private String companyName; // 회사 명
        private String manager; // 담당자
        private String phone; // 연락처

        public static CompanyResponse from(Member company) {
            return CompanyResponse.builder()
                .companyName(company.getCompanyInfo().getCompanyName())
                .manager(company.getCompanyInfo().getManager())
                .phone(company.getPhone())
                .build();
        }
    }

    @Getter
    @Builder
    public static class JobPostResponse {

        private Long jobPostId;
        private String title; // 제목
        private Tech tech; // 직종
        private Integer wage; // 일급
        private LocalTime startTime; // 시작 시간
        private LocalTime endTime; // 종료 시간

        private String workAddress; // 근무지
        private Double distance; // 근무지와의 거리

        private Boolean meal; // 식사 제공 여부
        private Boolean pickup; // 픽업 여부
        private List<String> pickupAddressList; // 픽업 장소 리스트
        private Park park; // 주차 가능 여부
        private String parkDetail; // 주차장 상세 설명
        private String preparation; // 준비 사항

        private List<String> imageUrls; // 이미지 정보

        private Boolean recruitmentFull; // 모집 인원 꽉 찼는지 여부
        private Boolean acceptedApplyOnDate; // 해당 날짜에 확정된 출역 건이 있는지 여부

        public static JobPostResponse from(OfferWorkDate offerWorkDate, List<Apply> acceptedApply, Location location) {
            WorkDate workDate = offerWorkDate.getWorkDate();
            JobPost jobPost = workDate.getJobPost();

            // 픽업 장소 리스트
            List<String> pickupAddressList = jobPost.getPickupList().stream()
                .map(Pickup::getAddress)
                .collect(Collectors.toList());

            // 이미지 리스트
            List<String> imageUrls = jobPost.getJobPostImageList().stream()
                .map(JobPostImage::getS3Url)
                .collect(Collectors.toList());

            // 인원이 꽉 찼을 경우 true
            Boolean recruitmentFull = workDate.getRecruitNum() <= workDate.getRegisteredNum();
            // 이미 확정된 내역이 있으면 true
            Boolean acceptedApplyOnDate = !acceptedApply.isEmpty();

            return JobPostResponse.builder()
                .jobPostId(jobPost.getId())
                .title(jobPost.getTitle())
                .tech(jobPost.getTech())
                .wage(jobPost.getWage())
                .startTime(jobPost.getStartTime())
                .endTime(jobPost.getEndTime())

                .workAddress(jobPost.getAddress().getAddress())
                .distance(DistanceCal.getDistance(jobPost, location))

                .meal(jobPost.getAvailableInfo().getMeal())
                .pickup(jobPost.getAvailableInfo().getPickup())
                .pickupAddressList(pickupAddressList)
                .park(jobPost.getAvailableInfo().getPark())
                .parkDetail(jobPost.getParkDetail())
                .preparation(jobPost.getPreparation())

                .imageUrls(imageUrls)

                .recruitmentFull(recruitmentFull)
                .acceptedApplyOnDate(acceptedApplyOnDate)

                .build();
        }
    }
}
