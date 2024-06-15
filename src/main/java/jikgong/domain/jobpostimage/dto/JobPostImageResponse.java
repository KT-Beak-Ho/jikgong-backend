package jikgong.domain.jobpostimage.dto;

import jikgong.domain.jobpostimage.entity.JobPostImage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JobPostImageResponse {

    private Long jobPostImageId;

    private String s3Url; // s3 url
    private boolean isThumbnail; // 썸네일 이미지 여부

    public static JobPostImageResponse from(JobPostImage jobPostImage) {
        return JobPostImageResponse.builder()
            .jobPostImageId(jobPostImage.getId())
            .s3Url(jobPostImage.getS3Url())
            .isThumbnail(jobPostImage.isThumbnail())
            .build();
    }
}
