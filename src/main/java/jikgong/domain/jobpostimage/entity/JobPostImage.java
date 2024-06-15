package jikgong.domain.jobpostimage.entity;


import jakarta.persistence.*;
import jikgong.domain.jobpost.entity.JobPost;
import jikgong.global.s3.ImageDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class JobPostImage {

    @Id
    @GeneratedValue
    @Column(name = "job_post_image")
    private Long id;

    private String storeImgName; // 저장된 이미지 명 (uuid)
    private String s3Url; // s3 url
    private boolean isThumbnail; // 썸네일 이미지 여부

    @ManyToOne
    @JoinColumn(name = "job_post_id")
    private JobPost jobPost;

    @Builder
    public JobPostImage(String storeImgName, String s3Url, boolean isThumbnail, JobPost jobPost) {
        this.storeImgName = storeImgName;
        this.s3Url = s3Url;
        this.isThumbnail = isThumbnail;
        this.jobPost = jobPost;
    }

    public static JobPostImage createEntity(ImageDto imageDto, JobPost jobPost) {
        return JobPostImage.builder()
            .storeImgName(imageDto.getStoreImgName())
            .s3Url(imageDto.getS3Url())
            .isThumbnail(imageDto.isThumbnail())
            .jobPost(jobPost)
            .build();
    }
}
