package jikgong.domain.jobpost.dto.worker;

import jikgong.domain.jobpost.entity.JobPost;
import jikgong.domain.jobpost.entity.Park;
import jikgong.domain.jobpost.entity.Tech;
import jikgong.domain.jobpostimage.entity.JobPostImage;
import jikgong.domain.location.entity.Location;
import jikgong.global.utils.DistanceCal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

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

    private Boolean isScrap; // 스크랩 여부

    private String thumbnailS3Url; // 썸네일 Url

    public void setScrap(Boolean scrap) {
        isScrap = scrap;
    }

    public static JobPostListResponse from(JobPost jobPost, Location location) {
        // JobPostImage 리스트가 null인 경우 null 반환
        List<JobPostImage> jobPostImages = jobPost.getJobPostImageList();
        String thumbnailS3Url = null;

        if (jobPostImages != null) {
            // Thumbnail 이미지 추출
            Optional<JobPostImage> thumbnailImage = jobPostImages.stream()
                    .filter(JobPostImage::isThumbnail)
                    .findFirst();

            // S3 URL 추출 및 변경, 없으면 null 반환
            thumbnailS3Url = thumbnailImage.map(JobPostImage::getS3Url)
                    .map(s3Url -> s3Url.replace("jikgong-image", "jikgong-resize-bucket"))
                    .orElse(null);
        }

        return JobPostListResponse.builder()
                .jobPostId(jobPost.getId())

                .tech(jobPost.getTech())
                .recruitNum(jobPost.getRecruitNum())
                .title(jobPost.getTitle())

                .meal(jobPost.getAvailableInfo().getMeal())
                .pickup(jobPost.getAvailableInfo().getPickup())
                .park(jobPost.getAvailableInfo().getPark())

                .startDate(jobPost.getStartDate())
                .endDate(jobPost.getEndDate())
                .startTime(jobPost.getStartTime())
                .endTime(jobPost.getEndTime())
                .address(jobPost.getAddress().getAddress())
                .distance((location == null) ? null : DistanceCal.getDistance(jobPost, location))

                .companyName(jobPost.getMember().getCompanyInfo().getCompanyName())
                .wage(jobPost.getWage())

                .isScrap(null)
                .thumbnailS3Url(thumbnailS3Url)

                .build();
    }
}
