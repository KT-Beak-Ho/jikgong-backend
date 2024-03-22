package jikgong.domain.jobPost.dtos.offer;

import jikgong.domain.addressInfo.entity.AddressInfo;
import jikgong.domain.addressInfo.entity.AddressType;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.Park;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.jobPostImage.entity.JobPostImage;
import jikgong.domain.location.entity.Location;
import jikgong.domain.member.entity.Member;
import jikgong.domain.offerWorkDate.entity.OfferWorkDate;
import jikgong.domain.workDate.dtos.WorkDateResponse;
import jikgong.domain.workDate.entity.WorkDate;
import jikgong.global.utils.DistanceCal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Builder
public class JobPostDetailResponseForOffer {
    private Long offerWorkDateId;
    private Long jobPostId;
    private String title; // 제목
    private Tech tech; // 직종
    private Integer wage; // 일급
    private WorkDateResponse workDateResponse; // 출역 제안 날짜
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

    private String companyName; // 회사 명
    private String manager; // 담당자
    private String phone; // 연락처

    private List<String> imageUrls; // 이미지 정보

    public static JobPostDetailResponseForOffer from(OfferWorkDate offerWorkDate, Location location) {
        WorkDate workDate = offerWorkDate.getWorkDate();
        JobPost jobPost = workDate.getJobPost();
        Member member = jobPost.getMember();

        // 픽업 장소 리스트
        List<String> pickupAddressList = jobPost.getAddressInfoList().stream()
                .filter(addressInfo -> addressInfo.getAddressType() == AddressType.PICK_UP)
                .map(AddressInfo::getAddress)
                .collect(Collectors.toList());

        // 이미지 리스트
        List<String> imageUrls = jobPost.getJobPostImageList().stream()
                .map(JobPostImage::getS3Url)
                .collect(Collectors.toList());

        return JobPostDetailResponseForOffer.builder()
                .offerWorkDateId(offerWorkDate.getId())
                .jobPostId(jobPost.getId())
                .title(jobPost.getTitle())
                .tech(jobPost.getTech())
                .wage(jobPost.getWage())
                .workDateResponse(WorkDateResponse.from(workDate))
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

                .companyName(member.getCompanyInfo().getCompanyName())
                .manager(member.getCompanyInfo().getManager())
                .phone(member.getPhone())

                .imageUrls(imageUrls)

                .build();
    }
}
