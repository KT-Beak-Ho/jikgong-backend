package jikgong.domain.jobPost.dtos.worker;

import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.Park;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.location.entity.Location;
import jikgong.domain.scrap.entity.Scrap;
import jikgong.global.utils.DistanceCal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Getter
@Builder
public class JobPostListResponse {
    private Long jobPostId;

    private Tech tech; // 직종
    private Integer recruitNum; // 모집 인원
    private String title; // 공고 제목

    // 가능 여부
    private Boolean meal; // 식사 제공 여부
    private Boolean pickup; // 픽업 여부
    private Park park; // 주차 가능 여부

    private LocalDate startDate; // 시작 날짜
    private LocalDate endDate; // 종료 날짜
    private LocalTime startTime; // 시작 시간
    private LocalTime endTime; // 종료 시간
    private String address; // 도로명 주소
    private Double distance; // 거리

    private String companyName;
    private Integer wage; // 임금

    // 스크랩 여부
    private Boolean isScrap;

    public void setScrap(Boolean scrap) {
        isScrap = scrap;
    }

//    public static JobPostListResponse from(Scrap scrap, Location location) {
//        JobPost jobPost = scrap.getJobPost();
//        return JobPostListResponse.builder()
//                .jobPostId(jobPost.getId())
//                .tech(jobPost.getTech())
//                .recruitNum(jobPost.getRecruitNum())
//                .title(jobPost.getTitle())
//                .meal(jobPost.getAvailableInfo().getMeal())
//                .pickup(jobPost.getAvailableInfo().getPickup())
//                .park(jobPost.getAvailableInfo().getPark())
//                .startDate(jobPost.getStartDate())
//                .endDate(jobPost.getEndDate())
//                .startTime(jobPost.getStartTime())
//                .endTime(jobPost.getEndTime())
//                .address(jobPost.getAddress().getAddress())
//                .distance(DistanceCal.getDistance(jobPost, location))
//                .companyName(jobPost.getMember().getCompanyInfo().getCompanyName())
//                .wage(jobPost.getWage())
//                .isScrap(true)
//                .build();
//    }
}
